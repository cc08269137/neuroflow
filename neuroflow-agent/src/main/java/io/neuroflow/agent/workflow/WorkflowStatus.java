package io.neuroflow.agent.workflow;

import java.time.Instant;

public class WorkflowStatus {
    public enum State {
        ACTIVE, DISABLED, DRAFT, DEPRECATED
    }

    private State state;
    private Instant lastExecutionTime;
    private Instant nextScheduleTime;
    private String lastExecutionResult;

    public WorkflowStatus() {
        this.state = State.ACTIVE;
    }

    // Getters and Setters
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public Instant getLastExecutionTime() { return lastExecutionTime; }
    public void setLastExecutionTime(Instant lastExecutionTime) { this.lastExecutionTime = lastExecutionTime; }
    public Instant getNextScheduleTime() { return nextScheduleTime; }
    public void setNextScheduleTime(Instant nextScheduleTime) { this.nextScheduleTime = nextScheduleTime; }
    public String getLastExecutionResult() { return lastExecutionResult; }
    public void setLastExecutionResult(String lastExecutionResult) { this.lastExecutionResult = lastExecutionResult; }
}