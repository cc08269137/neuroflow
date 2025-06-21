//package io.neuroflow.autoconfigure.agent;
//
//import io.neuroflow.agent.Workflow;
//import io.neuroflow.agent.WorkflowRegistry;
//import io.neuroflow.autoconfigure.agent.model.WorkflowDetail;
//import io.neuroflow.autoconfigure.agent.model.WorkflowInfo;
//
//import io.neuroflow.agent.workflow.WorkflowStatus;
//import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
//import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
//import org.springframework.boot.actuate.endpoint.annotation.Selector;
//import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
//import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
//import org.springframework.lang.Nullable;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * 提供工作流信息的 Actuator 端点
// */
//@WebEndpoint(id = "workflows")
//public class WorkflowEndpoint {
//
//    private final WorkflowRegistry workflowRegistry;
//
//    public WorkflowEndpoint(WorkflowRegistry workflowRegistry) {
//        this.workflowRegistry = workflowRegistry;
//    }
//
//    @ReadOperation
//    public List<WorkflowInfo> getAllWorkflows() {
//        return workflowRegistry.getAllWorkflows().stream()
//                .map(WorkflowInfo::fromWorkflow)
//                .collect(Collectors.toList());
//    }
//
//    @ReadOperation
//    public WorkflowDetail getWorkflow(@Selector String workflowId) {
//        Workflow workflow = workflowRegistry.getWorkflow(workflowId);
//        if (workflow == null) {
//            return null;
//        }
//        return WorkflowDetail.fromWorkflow(workflow);
//    }
//
//    @WriteOperation
//    public String updateWorkflowStatus(
//            @Selector String workflowId,
//            Map<String, String> request) {
//
//        Workflow workflow = workflowRegistry.getWorkflow(workflowId);
//        if (workflow == null) {
//            return "Workflow not found: " + workflowId;
//        }
//
//        String action = request.get("action");
//        if ("enable".equals(action)) {
//            workflow.getStatus().setState(WorkflowStatus.State.ACTIVE);
//            return "Workflow enabled: " + workflowId;
//        } else if ("disable".equals(action)) {
//            workflow.getStatus().setState(WorkflowStatus.State.DISABLED);
//            return "Workflow disabled: " + workflowId;
//        } else if ("deprecate".equals(action)) {
//            workflow.getStatus().setState(WorkflowStatus.State.DEPRECATED);
//            return "Workflow deprecated: " + workflowId;
//        }
//
//        return "Invalid action: " + action;
//    }
//
//    @WriteOperation
//    public String triggerWorkflow(
//            @Selector String workflowId,
//            Map<String, Object> inputs) {
//
//        Workflow workflow = workflowRegistry.getWorkflow(workflowId);
//        if (workflow == null) {
//            return "Workflow not found: " + workflowId;
//        }
//
//        // 实际实现中应异步触发工作流执行
//        return "Workflow triggered: " + workflowId;
//    }
//}