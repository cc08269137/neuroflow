package io.neuroflow.core.model;

import java.util.List;
import java.util.Map;

public class ModelRequest {
    private String model;
    private String prompt;
    private List<ChatMessage> messages;
    private Double temperature;
    private Integer maxTokens;
    private Boolean stream;
    private List<FunctionSpec> functions;
    private Map<String, Object> functionCall;

    // Getters and Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }
    public List<FunctionSpec> getFunctions() { return functions; }
    public void setFunctions(List<FunctionSpec> functions) { this.functions = functions; }
    public Map<String, Object> getFunctionCall() { return functionCall; }
    public void setFunctionCall(Map<String, Object> functionCall) { this.functionCall = functionCall; }
}
