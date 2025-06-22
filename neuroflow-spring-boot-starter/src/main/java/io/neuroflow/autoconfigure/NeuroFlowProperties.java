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
    private Logging logging = new Logging();

    @Getter
    @Setter
    public static class Gateway {
        private Models models = new Models();
        private List<String> modelPriority = new ArrayList<>();
        private int timeout = 5000;
        private int maxRetries = 3;
    }

    @Getter
    @Setter
    public static class Models {
        private OpenAIConfig openai;
        private QwenConfig qwen;
        private OllamaConfig ollama;
    }

    @Getter
    @Setter
    public static class OpenAIConfig {
        private String apiKey;
        private String endpoint = "https://api.openai.com/v1";
    }

    @Getter
    @Setter
    public static class QwenConfig {
        private String apiKey;
        private String endpoint = "dashscope.aliyuncs.com";
    }

    @Getter
    @Setter
    public static class OllamaConfig {
        private String baseUrl = "http://localhost:11434";
        private String model = "llama3";
    }

    @Getter
    @Setter
    public static class Agent {
        private boolean enabled = true;
        private String workflowDir = "classpath:/workflows";
        private boolean autoReload = true;
    }

    @Getter
    @Setter
    public static class Tools {
        private Text2SQL text2sql = new Text2SQL();
        private OCR ocr = new OCR();
    }

    @Getter
    @Setter
    public static class Text2SQL {
        private boolean enabled = true;
    }

    @Getter
    @Setter
    public static class OCR {
        private boolean enabled = false;
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
        private String password;
    }

    @Getter
    @Setter
    public static class Logging {
        private String level = "INFO";
        private String format = "json";
        private boolean enableMetrics = true;
    }
}