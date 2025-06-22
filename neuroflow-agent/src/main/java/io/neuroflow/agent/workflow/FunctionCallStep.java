package io.neuroflow.agent.workflow;

import io.neuroflow.core.NeuroFlowGateway;
import io.neuroflow.core.function.FunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class FunctionCallStep extends AbstractStep {
    private static final Logger log = LoggerFactory.getLogger(FunctionCallStep.class);
    private final String functionName;
    private final FunctionRegistry registry;
    private final NeuroFlowGateway gateway;

    public FunctionCallStep(String name, String functionName, FunctionRegistry registry, NeuroFlowGateway gateway) {
        super(name);
        this.functionName = functionName;
        this.registry = registry;
        this.gateway = gateway;
    }

    @Override
    protected Mono<Map<String, Object>> doExecute(Map<String, Object> context) {
        log.info("[FunctionCallStep] 执行: {}，函数: {}，参数: {}", name, functionName, context);
        return Mono.fromCallable(() -> {
            Object result = registry.call(functionName, context);
            log.info("[FunctionCallStep] {} 执行结果: {}", name, result);
            context.put(name + ".output", result);
            return createOutput(result);
        }).doOnError(e -> log.error("[FunctionCallStep] {} 执行异常", name, e));
    }

    @Override
    protected Flux<String> doStream(Map<String, Object> context) {
        // 通常函数调用不需要流式，直接返回Mono转Flux
        return doExecute(context).flatMapMany(map -> Flux.just(String.valueOf(map.get("output"))));
    }
} 