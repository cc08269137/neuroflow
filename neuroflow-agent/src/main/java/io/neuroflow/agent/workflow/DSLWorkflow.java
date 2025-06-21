package io.neuroflow.agent.workflow;

import io.neuroflow.agent.Workflow;
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
}