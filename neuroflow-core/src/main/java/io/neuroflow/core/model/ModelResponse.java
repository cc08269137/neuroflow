package io.neuroflow.core.model;

import java.util.List;
import java.util.Map;

public class ModelResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private FunctionCall functionCall;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }
    public Long getCreated() { return created; }
    public void setCreated(Long created) { this.created = created; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }
    public Usage getUsage() { return usage; }
    public void setUsage(Usage usage) { this.usage = usage; }
    public FunctionCall getFunctionCall() { return functionCall; }
    public void setFunctionCall(FunctionCall functionCall) { this.functionCall = functionCall; }

    // Inner classes
    public static class Choice {
        private Integer index;
        private ChatMessage message;
        private String finishReason;

        // Getters and Setters
        public Integer getIndex() { return index; }
        public void setIndex(Integer index) { this.index = index; }
        public ChatMessage getMessage() { return message; }
        public void setMessage(ChatMessage message) { this.message = message; }
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
    }

    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;

        // Getters and Setters
        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }
        public Integer getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }
        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
    }

    public static class FunctionCall {
        private String name;
        private Map<String, Object> arguments;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Map<String, Object> getArguments() { return arguments; }
        public void setArguments(Map<String, Object> arguments) { this.arguments = arguments; }
    }
}