package io.neuroflow.agent.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Predicate;

public class ConditionalStep extends AbstractStep {
    private static final Logger log = LoggerFactory.getLogger(ConditionalStep.class);
    private final Predicate<Map<String, Object>> condition;
    private final Step ifStep;
    private final Step elseStep;

    public ConditionalStep(String name, Predicate<Map<String, Object>> condition, Step ifStep, Step elseStep) {
        super(name);
        this.condition = condition;
        this.ifStep = ifStep;
        this.elseStep = elseStep;
    }

    @Override
    protected Mono<Map<String, Object>> doExecute(Map<String, Object> context) {
        boolean cond = condition.test(context);
        log.info("[ConditionalStep] 执行: {}，条件结果: {}", name, cond);
        if (cond) {
            return ifStep.execute(context).doOnSuccess(r -> log.info("[ConditionalStep] {} 进入if分支", name));
        } else {
            return elseStep.execute(context).doOnSuccess(r -> log.info("[ConditionalStep] {} 进入else分支", name));
        }
    }

    @Override
    protected Flux<String> doStream(Map<String, Object> context) {
        boolean cond = condition.test(context);
        log.info("[ConditionalStep] 流式执行: {}，条件结果: {}", name, cond);
        if (cond) {
            return ifStep.stream(context);
        } else {
            return elseStep.stream(context);
        }
    }
} 