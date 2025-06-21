package io.neuroflow.agent.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public abstract class AbstractStep implements Step {
    protected final Logger log;
    protected final String name;

    public AbstractStep(String name) {
        this.name = name;
        this.log = LoggerFactory.getLogger(getClass());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Mono<Map<String, Object>> execute(Map<String, Object> context) {
        log.info("Executing step: {}", name);
        try {
            return doExecute(context)
                    .onErrorResume(e -> handleStepError(e, context))
                    .doOnSuccess(result -> log.info("Step {} completed successfully", name))
                    .doOnError(e -> log.error("Step {} failed", name, e));
        } catch (Exception e) {
            return handleStepError(e, context);
        }
    }
    @Override
    public Flux<String> stream(Map<String, Object> context) {
        log.info("Streaming step: {}", name);
        return doStream(context)
                .doOnError(e -> log.error("Step {} streaming failed", name, e));
    }

    protected abstract Mono<Map<String, Object>> doExecute(Map<String, Object> context);
    protected abstract Flux<String> doStream(Map<String, Object> context);

    protected Map<String, Object> createOutput(Object result) {
        return Map.of(
                "step", name,
                "output", result,
                "timestamp", System.currentTimeMillis()
        );
    }

    protected Map<String, Object> createErrorOutput(Throwable error) {
        return Map.of(
                "step", name,
                "error", error.getMessage(),
                "timestamp", System.currentTimeMillis()
        );
    }

    private Mono<Map<String, Object>> handleStepError(Throwable e, Map<String, Object> context) {
        if (e instanceof WorkflowExecutionException) {
            return Mono.error(e);
        }

        WorkflowExecutionException ex = WorkflowExecutionException.stepFailure(
                getWorkflowId(context), name, "Step execution failed: " + e.getMessage(),
                context, e
        );
        return Mono.error(ex);
    }

    private String getWorkflowId(Map<String, Object> context) {
        // 尝试从上下文获取工作流ID
        if (context.containsKey("workflowId")) {
            return context.get("workflowId").toString();
        }
        return "unknown";
    }
}