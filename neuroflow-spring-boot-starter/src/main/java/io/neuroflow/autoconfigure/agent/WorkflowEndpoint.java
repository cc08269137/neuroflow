package io.neuroflow.autoconfigure.agent;

import io.neuroflow.agent.AgentOrchestrator;
import io.neuroflow.agent.WorkflowRegistry;
import io.neuroflow.agent.model.WorkflowDetail;
import io.neuroflow.agent.workflow.WorkflowExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工作流 REST API 端点
 * 提供工作流的注册、执行、查询等功能
 */
@RestController
@RequestMapping("/api/workflows")
public class WorkflowEndpoint {
    private static final Logger log = LoggerFactory.getLogger(WorkflowEndpoint.class);

    private final WorkflowRegistry workflowRegistry;
    private final AgentOrchestrator orchestrator;

    public WorkflowEndpoint(WorkflowRegistry workflowRegistry, AgentOrchestrator orchestrator) {
        this.workflowRegistry = workflowRegistry;
        this.orchestrator = orchestrator;
        log.info("[WorkflowEndpoint] 初始化完成");
    }

    /**
     * 获取所有工作流列表
     */
    @GetMapping
    public Mono<ResponseEntity<List<WorkflowDetail>>> listWorkflows() {
        log.info("[WorkflowEndpoint] 获取工作流列表");
        try {
            List<WorkflowDetail> workflows = workflowRegistry.getAllWorkflows().stream()
                    .map(WorkflowDetail::fromWorkflow)
                    .collect(Collectors.toList());
            log.info("[WorkflowEndpoint] 返回工作流列表，数量: {}", workflows.size());
            return Mono.just(ResponseEntity.ok(workflows));
        } catch (Exception e) {
            log.error("[WorkflowEndpoint] 获取工作流列表失败", e);
            return Mono.just(ResponseEntity.internalServerError().build());
        }
    }

    /**
     * 获取指定工作流详情
     */
    @GetMapping("/{workflowId}")
    public Mono<ResponseEntity<WorkflowDetail>> getWorkflow(@PathVariable String workflowId) {
        log.info("[WorkflowEndpoint] 获取工作流详情: {}", workflowId);
        try {
            var workflow = workflowRegistry.getWorkflow(workflowId);
            if (workflow == null) {
                log.warn("[WorkflowEndpoint] 工作流不存在: {}", workflowId);
                return Mono.just(ResponseEntity.notFound().build());
            }
            WorkflowDetail detail = WorkflowDetail.fromWorkflow(workflow);
            log.info("[WorkflowEndpoint] 返回工作流详情: {}", workflowId);
            return Mono.just(ResponseEntity.ok(detail));
        } catch (Exception e) {
            log.error("[WorkflowEndpoint] 获取工作流详情失败: {}", workflowId, e);
            return Mono.just(ResponseEntity.internalServerError().build());
        }
    }

    /**
     * 执行工作流
     */
    @PostMapping("/{workflowId}/execute")
    public Mono<ResponseEntity<Object>> executeWorkflow(
            @PathVariable String workflowId,
            @RequestBody Map<String, Object> inputs) {
        log.info("[WorkflowEndpoint] 执行工作流: {}，输入参数: {}", workflowId, inputs);
        return orchestrator.execute(workflowId, inputs)
                .map(result -> {
                    log.info("[WorkflowEndpoint] 工作流执行成功: {}，结果: {}", workflowId, result);
                    return ResponseEntity.ok(result);
                })
                .onErrorResume(WorkflowExecutionException.class, e -> {
                    log.error("[WorkflowEndpoint] 工作流执行异常: {}，错误类型: {}，消息: {}", 
                            workflowId, e.getErrorType(), e.getMessage(), e);
                    return Mono.just(ResponseEntity.badRequest().body(Map.of(
                            "error", e.getMessage(),
                            "errorType", e.getErrorType().name(),
                            "workflowId", workflowId,
                            "stepName", e.getStepName()
                    )));
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("[WorkflowEndpoint] 工作流执行系统异常: {}", workflowId, e);
                    return Mono.just(ResponseEntity.internalServerError().body(Map.of(
                            "error", "Internal server error",
                            "workflowId", workflowId
                    )));
                });
    }

    /**
     * 流式执行工作流
     */
    @PostMapping(value = "/{workflowId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamWorkflow(
            @PathVariable String workflowId,
            @RequestBody Map<String, Object> inputs) {
        log.info("[WorkflowEndpoint] 流式执行工作流: {}，输入参数: {}", workflowId, inputs);
        return orchestrator.stream(workflowId, inputs)
                .doOnNext(token -> log.debug("[WorkflowEndpoint] 流式响应: {} -> {}", workflowId, token))
                .doOnComplete(() -> log.info("[WorkflowEndpoint] 流式执行完成: {}", workflowId))
                .doOnError(e -> log.error("[WorkflowEndpoint] 流式执行异常: {}", workflowId, e));
    }

    /**
     * 获取工作流执行统计
     */
    @GetMapping("/{workflowId}/stats")
    public Mono<ResponseEntity<AgentOrchestrator.WorkflowExecutionStats>> getWorkflowStats(@PathVariable String workflowId) {
        log.info("[WorkflowEndpoint] 获取工作流统计: {}", workflowId);
        try {
            var stats = orchestrator.getExecutionStats(workflowId);
            log.info("[WorkflowEndpoint] 返回工作流统计: {}，成功: {}，失败: {}，成功率: {}%", 
                    workflowId, stats.getSuccessCount(), stats.getFailureCount(), stats.getSuccessRate());
            return Mono.just(ResponseEntity.ok(stats));
        } catch (Exception e) {
            log.error("[WorkflowEndpoint] 获取工作流统计失败: {}", workflowId, e);
            return Mono.just(ResponseEntity.internalServerError().build());
        }
    }

    /**
     * 重置工作流统计
     */
    @DeleteMapping("/{workflowId}/stats")
    public Mono<ResponseEntity<Void>> resetWorkflowStats(@PathVariable String workflowId) {
        log.info("[WorkflowEndpoint] 重置工作流统计: {}", workflowId);
        try {
            orchestrator.resetStats(workflowId);
            log.info("[WorkflowEndpoint] 工作流统计重置成功: {}", workflowId);
            return Mono.just(ResponseEntity.ok().build());
        } catch (Exception e) {
            log.error("[WorkflowEndpoint] 重置工作流统计失败: {}", workflowId, e);
            return Mono.just(ResponseEntity.internalServerError().build());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        log.debug("[WorkflowEndpoint] 健康检查");
        Map<String, Object> health = Map.of(
                "status", "UP",
                "workflowCount", workflowRegistry.getAllWorkflows().size(),
                "timestamp", System.currentTimeMillis()
        );
        return Mono.just(ResponseEntity.ok(health));
    }
}
