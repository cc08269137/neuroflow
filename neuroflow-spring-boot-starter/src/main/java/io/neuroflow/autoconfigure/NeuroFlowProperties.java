package io.neuroflow.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "neuroflow")
public class NeuroFlowProperties {
    private Gateway gateway = new Gateway();
    private Agent agent = new Agent();
    private Tools tools = new Tools();
    private VectorStore vectorStore = new VectorStore();

    @Getter
    @Setter
    public static class Gateway {
        private Models models = new Models();
        private List<String> modelPriority = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Models {
        private OpenAIConfig openai;
        private QwenConfig qwen;
    }

    @Getter
    @Setter
    public static class OpenAIConfig {
        private String apiKey;
    }

    @Getter
    @Setter
    public static class QwenConfig {
        private String apiKey;
        private String endpoint = "dashscope.aliyuncs.com";
    }

    @Getter
    @Setter
    public static class Agent {
        private String workflowDir = "classpath:/workflows";
    }

    @Getter
    @Setter
    public static class Tools {
        private boolean text2sql = false;
    }

    @Getter
    @Setter
    public static class VectorStore {
        private String type = "redis";
        private RedisConfig redis = new RedisConfig();
    }

    @Getter
    @Setter
    public static class RedisConfig {
        private String host = "localhost";
        private int port = 6379;
        private String indexName = "neuroflow-vectors";
    }
}