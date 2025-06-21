package io.neuroflow.agent.workflow;

import org.springframework.web.ErrorResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 工作流执行异常
 *
 * <p>表示在工作流执行过程中发生的特定错误，包含工作流ID和错误上下文信息
 */
public class WorkflowExecutionException extends RuntimeException {
    private final String workflowId;
    private final String stepName;
    private final ErrorType errorType;
    private final Map<String, Object> context;



    /**
     * 错误类型枚举
     */
    public enum ErrorType {
        WORKFLOW_NOT_FOUND,
        STEP_EXECUTION_FAILED,
        TIMEOUT,
        INPUT_VALIDATION,
        MODEL_ERROR,
        FUNCTION_ERROR,
        CONTEXT_MISSING,
        UNKNOWN
    }

    /**
     * 构造方法
     * @param workflowId 工作流ID
     * @param stepName 发生错误的步骤名称
     * @param errorType 错误类型
     * @param message 错误消息
     * @param context 错误发生时的上下文信息
     * @param cause 原始异常
     */
    public WorkflowExecutionException(String workflowId,
                                      String stepName,
                                      ErrorType errorType,
                                      String message,
                                      Map<String, Object> context,
                                      Throwable cause) {
        super(buildMessage(workflowId, stepName, errorType, message), cause);
        this.workflowId = workflowId;
        this.stepName = stepName;
        this.errorType = errorType;
        this.context = context != null ? new HashMap<>(context) : new HashMap<>();
    }

    private static String buildMessage(String workflowId, String stepName, ErrorType errorType, String message) {
        return String.format("[Workflow: %s | Step: %s | Type: %s] %s",
                workflowId,
                stepName != null ? stepName : "N/A",
                errorType.name(),
                message);
    }

    // 便捷构造方法

    public static WorkflowExecutionException workflowNotFound(String workflowId) {
        return new WorkflowExecutionException(
                workflowId, null, ErrorType.WORKFLOW_NOT_FOUND,
                "Workflow not found in registry", null, null
        );
    }

    public static WorkflowExecutionException stepFailure(String workflowId, String stepName,
                                                         String message, Map<String, Object> context,
                                                         Throwable cause) {
        return new WorkflowExecutionException(
                workflowId, stepName, ErrorType.STEP_EXECUTION_FAILED,
                message, context, cause
        );
    }

    public static WorkflowExecutionException timeout(String workflowId, String stepName,
                                                     long timeoutMs, Map<String, Object> context) {
        return new WorkflowExecutionException(
                workflowId, stepName, ErrorType.TIMEOUT,
                "Execution timed out after " + timeoutMs + "ms", context, null
        );
    }

    public static WorkflowExecutionException inputValidation(String workflowId, String stepName,
                                                             String field, Object value,
                                                             String message) {
        Map<String, Object> context = Map.of(
                "field", field,
                "value", value
        );
        return new WorkflowExecutionException(
                workflowId, stepName, ErrorType.INPUT_VALIDATION,
                "Input validation failed: " + message, context, null
        );
    }

    // Getters
    public String getWorkflowId() { return workflowId; }
    public String getStepName() { return stepName; }
    public ErrorType getErrorType() { return errorType; }
    public Map<String, Object> getContext() { return Collections.unmodifiableMap(context); }

    /**
     * 添加额外的上下文信息
     * @param key 键
     * @param value 值
     * @return 当前异常实例（用于链式调用）
     */
    public WorkflowExecutionException addContext(String key, Object value) {
        this.context.put(key, value);
        return this;
    }

    /**
     * 转换为错误响应DTO
     * @return 错误响应对象
     */
//    public ErrorResponse toErrorResponse() {
//        return new ErrorResponse(
//                getMessage(),
//                workflowId,
//                stepName,
//                errorType.name(),
//                context
//        );
//    }

    /**
     * 错误响应DTO
     */
//    public record ErrorResponse(
//            String message,
//            String workflowId,
//            String stepName,
//            String errorType,
//            Map<String, Object> context
//    ) {}
}