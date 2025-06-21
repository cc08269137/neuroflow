package io.neuroflow.core.model;

import java.util.List;
import java.util.Map;

public class FunctionSpec {
    private String name;
    private String description;
    private Map<String, Object> parameters; // JSON schema

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}