package io.neuroflow.autoconfigure;

import io.neuroflow.core.NeuroFlowGateway;
import io.neuroflow.core.client.ModelClient;
import io.neuroflow.core.client.impl.OpenAIClient;
import io.neuroflow.core.client.impl.QwenClient;
import io.neuroflow.core.function.FunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(NeuroFlowProperties.class)
public class NeuroFlowAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(NeuroFlowAutoConfiguration.class);

    private final NeuroFlowProperties properties;

    public NeuroFlowAutoConfiguration(NeuroFlowProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public FunctionRegistry functionRegistry() {
        return new FunctionRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public Map<String, ModelClient> modelClients() {
        Map<String, ModelClient> clients = new HashMap<>();

        // OpenAI 配置
        if (properties.getGateway().getModels().getOpenai() != null) {
            NeuroFlowProperties.OpenAIConfig openai = properties.getGateway().getModels().getOpenai();
            clients.put("openai", new OpenAIClient(openai.getApiKey()));
            log.info("OpenAI client initialized with API key");
        }

        // Qwen 配置
        if (properties.getGateway().getModels().getQwen() != null) {
            NeuroFlowProperties.QwenConfig qwen = properties.getGateway().getModels().getQwen();
            clients.put("qwen", new QwenClient(qwen.getApiKey(), qwen.getEndpoint()));
            log.info("Qwen client initialized with endpoint: {}", qwen.getEndpoint());
        }

        return clients;
    }

    @Bean
    @ConditionalOnMissingBean
    public NeuroFlowGateway neuroFlowGateway(Map<String, ModelClient> modelClients,
                                             NeuroFlowProperties properties) {
        List<String> modelPriority = properties.getGateway().getModelPriority();
        log.info("NeuroFlowGateway initialized with priority: {}", modelPriority);
        return new NeuroFlowGateway(modelClients, modelPriority);
    }
}