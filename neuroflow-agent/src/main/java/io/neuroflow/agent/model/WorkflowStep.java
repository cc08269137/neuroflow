package io.neuroflow.agent.model;

public enum WorkflowStep {
    LLM_CALL,
    FUNCTION_CALL,
    CONDITIONAL,
    PARALLEL,
    ERROR_HANDLER
}
