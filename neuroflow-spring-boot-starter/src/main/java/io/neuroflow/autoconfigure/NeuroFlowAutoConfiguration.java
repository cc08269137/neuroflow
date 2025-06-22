package io.neuroflow.autoconfigure;

import io.neuroflow.core.NeuroFlowGateway;
import io.neuroflow.core.client.ModelClient;
import io.neuroflow.core.client.impl.OpenAIClient;
import io.neuroflow.core.client.impl.QwenClient;
import io.neuroflow.core.client.impl.OllamaClient;
import io.neuroflow.core.function.FunctionRegistry;
import io.neuroflow.core.function.AIFunction;
import io.neuroflow.core.function.FunctionDescriptor;
import io.neuroflow.core.function.FunctionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import java.lang.reflect.Method;

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

        // Ollama 配置
        if (properties.getGateway().getModels().getOllama() != null) {
            NeuroFlowProperties.OllamaConfig ollama = properties.getGateway().getModels().getOllama();
            clients.put("ollama", new OllamaClient(ollama.getBaseUrl(), ollama.getModel()));
            log.info("Ollama client initialized with baseUrl: {}, model: {}", ollama.getBaseUrl(), ollama.getModel());
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

    @Bean
    public BeanPostProcessor aiFunctionAutoRegistrar(FunctionRegistry functionRegistry) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                Class<?> clazz = bean.getClass();
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(AIFunction.class)) {
                        AIFunction aiFunction = method.getAnnotation(AIFunction.class);
                        String name = aiFunction.name();
                        String desc = aiFunction.description();
                        try {
                            FunctionDescriptor descriptor = new FunctionDescriptor();
                            descriptor.setName(name);
                            descriptor.setDescription(desc);
                            descriptor.setMethod(method);
                            descriptor.setTarget(bean);
                            // 参数解析
                            var params = new java.util.ArrayList<FunctionParameter>();
                            for (var param : method.getParameters()) {
                                params.add(FunctionParameter.fromMethodParameter(param));
                            }
                            descriptor.setParameters(params);
                            functionRegistry.registerFunction(name, descriptor);
                            log.info("[AIFunction] 注册成功: {} (bean: {}, method: {})", name, beanName, method.getName());
                        } catch (Exception e) {
                            log.error("[AIFunction] 注册失败: {} (bean: {}, method: {})", name, beanName, method.getName(), e);
                        }
                    }
                }
                return bean;
            }
        };
    }
}