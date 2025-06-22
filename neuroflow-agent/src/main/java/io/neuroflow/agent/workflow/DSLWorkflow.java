package io.neuroflow.agent.workflow;

import io.neuroflow.agent.workflow.Workflow;
import io.neuroflow.core.NeuroFlowGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSLWorkflow implements Workflow {
    private static final Logger log = LoggerFactory.getLogger(DSLWorkflow.class);

    private final String id;
    private final List<Step> steps = new ArrayList<>();
    private final NeuroFlowGateway gateway;

    public DSLWorkflow(String id, NeuroFlowGateway gateway) {
        this.id = id;
        this.gateway = gateway;
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return id; // 使用ID作为名称
    }

    @Override
    public String getDescription() {
        return "DSL Workflow: " + id;
    }

    @Override
    public int getStepCount() {
        return steps.size();
    }

    @Override
    public Mono<Object> execute(Map<String, Object> inputs) {
        Map<String, Object> context = new HashMap<>(inputs);

        return Flux.fromIterable(steps)
                .flatMapSequential(step -> step.execute(context))
                .then(Mono.fromCallable(() -> context.get("output")))
                .doOnSuccess(result -> log.info("Workflow {} completed successfully", id))
                .doOnError(e -> log.error("Workflow {} failed", id, e));
    }

    @Override
    public Flux<String> stream(Map<String, Object> inputs) {
        Map<String, Object> context = new HashMap<>(inputs);
        context.put("streaming", true);

        return Flux.fromIterable(steps)
                .flatMapSequential(step -> step.stream(context))
                .doOnError(e -> log.error("Workflow {} streaming failed", id, e));
    }

    @Override
    public WorkflowMetadata getMetadata() {
        return new WorkflowMetadata(id, "DSLWorkflow", "DSL Workflow: " + id);
    }

    @Override
    public WorkflowStatus getStatus() {
        return new WorkflowStatus();
    }

    // 链式API
    public DSLWorkflow llmStep(String name, String prompt, String model) {
        log.info("[DSLWorkflow] 添加 LLM 步骤: {} (model: {})", name, model);
        steps.add(new LLMCallStep(name, prompt, model, gateway));
        return this;
    }
    public DSLWorkflow functionStep(String name, String functionName, io.neuroflow.core.function.FunctionRegistry registry) {
        log.info("[DSLWorkflow] 添加函数调用步骤: {} (function: {})", name, functionName);
        steps.add(new FunctionCallStep(name, functionName, registry, gateway));
        return this;
    }
    public DSLWorkflow conditionalStep(String name, java.util.function.Predicate<java.util.Map<String, Object>> condition, Step ifStep, Step elseStep) {
        log.info("[DSLWorkflow] 添加条件分支步骤: {}", name);
        steps.add(new ConditionalStep(name, condition, ifStep, elseStep));
        return this;
    }
    public DSLWorkflow step(Step step) {
        log.info("[DSLWorkflow] 添加自定义步骤: {}", step.getName());
        steps.add(step);
        return this;
    }
    public DSLWorkflow build() {
        log.info("[DSLWorkflow] 构建完成，步骤数: {}", steps.size());
        return this;
    }
}