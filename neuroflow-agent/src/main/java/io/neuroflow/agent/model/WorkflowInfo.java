package io.neuroflow.autoconfigure.agent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.neuroflow.agent.workflow.WorkflowMetadata;
import io.neuroflow.agent.workflow.WorkflowStatus;
import org.springframework.lang.Nullable;

import java.time.Instant;

/**
 * 工作流摘要信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowInfo {
    private final String id;
    private final String name;
    private final int stepCount;
    private final String description;
    private final String status;
    private final Instant createdAt;
    private final Instant lastModified;
    private final String version;
    private final String createdBy;

    public WorkflowInfo(String id, String name, int stepCount,
                        @Nullable String description, String status,
                        Instant createdAt, Instant lastModified,
                        @Nullable String version, @Nullable String createdBy) {
        this.id = id;
        this.name = name;
        this.stepCount = stepCount;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.version = version;
        this.createdBy = createdBy;
    }

    public static WorkflowInfo fromWorkflow(io.neuroflow.agent.workflow.Workflow workflow) {
        WorkflowMetadata metadata = workflow.getMetadata();
        WorkflowStatus status = workflow.getStatus();

        return new WorkflowInfo(
                workflow.getId(),
                workflow.getName(),
                workflow.getStepCount(),
                metadata.getDescription(),
                status.getState().name(),
                metadata.getCreatedAt(),
                metadata.getLastModified(),
                metadata.getVersion(),
                metadata.getCreatedBy()
        );
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getStepCount() { return stepCount; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastModified() { return lastModified; }
    public String getVersion() { return version; }
    public String getCreatedBy() { return createdBy; }
}