package io.neuroflow.agent.workflow;

import java.time.Instant;
import java.util.Map;

public class WorkflowMetadata {
    private String id;
    private String name;
    private String description;
    private String createdBy;
    private Instant createdAt;
    private Instant lastModified;
    private Map<String, Object> properties;
    private String version;

    public WorkflowMetadata() {
        // 默认构造函数
    }

    public WorkflowMetadata(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = Instant.now();
        this.lastModified = Instant.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getLastModified() { return lastModified; }
    public void setLastModified(Instant lastModified) { this.lastModified = lastModified; }
    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
}