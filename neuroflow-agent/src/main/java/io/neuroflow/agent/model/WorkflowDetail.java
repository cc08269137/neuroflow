package io.neuroflow.autoconfigure.agent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.neuroflow.agent.workflow.WorkflowMetadata;
import io.neuroflow.agent.workflow.WorkflowStatus;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Map;

/**
 * 工作流详细信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowDetail {
    private final String id;
    private final String name;
    private final String description;
    private final int stepCount;
    private final String status;
    private final Instant createdAt;
    private final Instant lastModified;
    private final String createdBy;
    private final String version;
    private final Map<String, Object> properties;
    private final Instant lastExecutionTime;
    private final String lastExecutionResult;
    private final Instant nextScheduleTime;

    public WorkflowDetail(String id, String name, String description, int stepCount,
                          String status, Instant createdAt, Instant lastModified,
                          @Nullable String createdBy, @Nullable String version,
                          Map<String, Object> properties,
                          @Nullable Instant lastExecutionTime,
                          @Nullable String lastExecutionResult,
                          @Nullable Instant nextScheduleTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stepCount = stepCount;
        this.status = status;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.createdBy = createdBy;
        this.version = version;
        this.properties = properties;
        this.lastExecutionTime = lastExecutionTime;
        this.lastExecutionResult = lastExecutionResult;
        this.nextScheduleTime = nextScheduleTime;
    }

    public static WorkflowDetail fromWorkflow(io.neuroflow.agent.workflow.Workflow workflow) {
        WorkflowMetadata metadata = workflow.getMetadata();
        WorkflowStatus status = workflow.getStatus();

        return new WorkflowDetail(
                workflow.getId(),
                workflow.getName(),
                metadata.getDescription(),
                workflow.getStepCount(),
                status.getState().name(),
                metadata.getCreatedAt(),
                metadata.getLastModified(),
                metadata.getCreatedBy(),
                metadata.getVersion(),
                metadata.getProperties(),
                status.getLastExecutionTime(),
                status.getLastExecutionResult(),
                status.getNextScheduleTime()
        );
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getStepCount() { return stepCount; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastModified() { return lastModified; }
    public String getCreatedBy() { return createdBy; }
    public String getVersion() { return version; }
    public Map<String, Object> getProperties() { return properties; }
    public Instant getLastExecutionTime() { return lastExecutionTime; }
    public String getLastExecutionResult() { return lastExecutionResult; }
    public Instant getNextScheduleTime() { return nextScheduleTime; }
}