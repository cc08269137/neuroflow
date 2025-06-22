package io.neuroflow.agent.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

public class ErrorHandlingStep extends AbstractStep {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingStep.class);
    private final Step mainStep;
    private final Function<Throwable, Step> fallbackProvider;

    public ErrorHandlingStep(String name, Step mainStep, Function<Throwable, Step> fallbackProvider) {
        super(name);
        this.mainStep = mainStep;
        this.fallbackProvider = fallbackProvider;
    }

    @Override
    protected Mono<Map<String, Object>> doExecute(Map<String, Object> context) {
        log.info("[ErrorHandlingStep] 执行: {}，主步骤: {}", name, mainStep.getName());
        return mainStep.execute(context)
                .onErrorResume(e -> {
                    log.error("[ErrorHandlingStep] {} 主步骤异常，进入回退: {}", name, e.getMessage(), e);
                    Step fallback = fallbackProvider.apply(e);
                    return fallback.execute(context).doOnSuccess(r -> log.info("[ErrorHandlingStep] {} 回退成功", name));
                });
    }

    @Override
    protected Flux<String> doStream(Map<String, Object> context) {
        log.info("[ErrorHandlingStep] 流式执行: {}，主步骤: {}", name, mainStep.getName());
        return mainStep.stream(context)
                .onErrorResume(e -> {
                    log.error("[ErrorHandlingStep] {} 主步骤流式异常，进入回退: {}", name, e.getMessage(), e);
                    Step fallback = fallbackProvider.apply(e);
                    return fallback.stream(context);
                });
    }
} 