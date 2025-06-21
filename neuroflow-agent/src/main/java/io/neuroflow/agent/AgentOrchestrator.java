package io.neuroflow.agent;


import io.neuroflow.agent.workflow.WorkflowExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工作流协调器，负责执行和监控工作流
 */
@Component
public class AgentOrchestrator {
    private static final Logger log = LoggerFactory.getLogger(AgentOrchestrator.class);

    private final WorkflowRegistry workflowRegistry;
    private final Map<String, WorkflowExecutionStats> executionStats = new ConcurrentHashMap<>();

    public AgentOrchestrator(WorkflowRegistry workflowRegistry) {
        this.workflowRegistry = workflowRegistry;
    }

    /**
     * 执行工作流
     * @param workflowId 工作流ID
     * @param inputs 输入参数
     * @return 执行结果
     */
    public Mono<Object> execute(String workflowId, Map<String, Object> inputs) {
        Workflow workflow = workflowRegistry.getWorkflow(workflowId);
        if (workflow == null) {
            log.error("Workflow not found: {}", workflowId);
            return Mono.error(WorkflowExecutionException.workflowNotFound(workflowId));
        }

        long startTime = System.currentTimeMillis();
        log.info("Executing workflow: {}", workflowId);

        return workflow.execute(inputs)
                .onErrorResume(e -> handleExecutionError(e, workflowId, null))
                .doOnSuccess(result -> recordSuccess(workflowId, startTime))
                .doOnError(e -> recordFailure(workflowId, startTime, e))
                .timeout(Duration.ofSeconds(120),
                        Mono.defer(() -> {
                            long duration = System.currentTimeMillis() - startTime;
                            WorkflowExecutionException ex = WorkflowExecutionException.timeout(
                                    workflowId, null, duration, inputs
                            );
                            return Mono.error(ex);
                        })
                );
    }
    /**
     * 流式执行工作流
     * @param workflowId 工作流ID
     * @param inputs 输入参数
     * @return 流式响应
     */
    public Flux<String> stream(String workflowId, Map<String, Object> inputs) {
        Workflow workflow = workflowRegistry.getWorkflow(workflowId);
        if (workflow == null) {
            log.error("Workflow not found: {}", workflowId);
            return Flux.error(WorkflowExecutionException.workflowNotFound(workflowId));
        }

        long startTime = System.currentTimeMillis();
        log.info("Streaming workflow: {}", workflowId);

        return workflow.stream(inputs)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    recordExecution(workflowId, true, duration);
                    log.info("Workflow {} streaming completed in {} ms", workflowId, duration);
                })
                .doOnError(e -> {
                    long duration = System.currentTimeMillis() - startTime;
                    recordExecution(workflowId, false, duration);
                    log.error("Workflow {} streaming failed after {} ms", workflowId, duration, e);
                });
    }
    private Mono<Object> handleExecutionError(Throwable e, String workflowId, String stepName) {
        if (e instanceof WorkflowExecutionException) {
            return Mono.error(e);
        }

        // 包装通用异常
        WorkflowExecutionException ex = WorkflowExecutionException.stepFailure(
                workflowId, stepName, "Step execution failed: " + e.getMessage(),
                null, e
        );
        return Mono.error(ex);
    }

    private void recordSuccess(String workflowId, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        WorkflowExecutionStats stats = executionStats.computeIfAbsent(
                workflowId, id -> new WorkflowExecutionStats()
        );
        stats.recordSuccess(duration);
        log.info("Workflow {} completed in {} ms", workflowId, duration);
    }
    /**
     * 获取工作流执行统计
     * @param workflowId 工作流ID
     * @return 执行统计信息
     */
    public WorkflowExecutionStats getExecutionStats(String workflowId) {
        return executionStats.getOrDefault(workflowId, new WorkflowExecutionStats());
    }

    /**
     * 重置工作流统计
     * @param workflowId 工作流ID
     */
    public void resetStats(String workflowId) {
        executionStats.remove(workflowId);
        log.info("Reset stats for workflow: {}", workflowId);
    }

    private void recordExecution(String workflowId, boolean success, long duration) {
        WorkflowExecutionStats stats = executionStats.computeIfAbsent(
                workflowId, id -> new WorkflowExecutionStats()
        );

        if (success) {
            stats.recordSuccess(duration);
        } else {
            stats.recordFailure(duration);
        }
    }
    private void recordFailure(String workflowId, long startTime, Throwable e) {
        long duration = System.currentTimeMillis() - startTime;
        WorkflowExecutionStats stats = executionStats.computeIfAbsent(
                workflowId, id -> new WorkflowExecutionStats()
        );
        stats.recordFailure(duration);

        if (e instanceof WorkflowExecutionException) {
            WorkflowExecutionException ex = (WorkflowExecutionException) e;
            log.error("Workflow {} failed at step {} after {} ms: [{}] {}",
                    workflowId, ex.getStepName(), duration, ex.getErrorType(), ex.getMessage());
        } else {
            log.error("Workflow {} failed after {} ms", workflowId, duration, e);
        }
    }

    /**
     * 工作流执行统计类
     */
    public static class WorkflowExecutionStats {
        private int successCount;
        private int failureCount;
        private long totalDuration;
        private long minDuration = Long.MAX_VALUE;
        private long maxDuration;

        public synchronized void recordSuccess(long duration) {
            successCount++;
            updateDurationStats(duration);
        }

        public synchronized void recordFailure(long duration) {
            failureCount++;
            updateDurationStats(duration);
        }

        private void updateDurationStats(long duration) {
            totalDuration += duration;
            minDuration = Math.min(minDuration, duration);
            maxDuration = Math.max(maxDuration, duration);
        }

        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public int getTotalCount() { return successCount + failureCount; }
        public double getSuccessRate() {
            return getTotalCount() > 0 ?
                    (double) successCount / getTotalCount() * 100 : 0.0;
        }
        public long getAverageDuration() {
            return getTotalCount() > 0 ? totalDuration / getTotalCount() : 0;
        }
        public long getMinDuration() { return minDuration == Long.MAX_VALUE ? 0 : minDuration; }
        public long getMaxDuration() { return maxDuration; }
    }
}