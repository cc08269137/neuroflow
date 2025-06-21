package io.neuroflow.agent.workflow;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface Step {
    String getName();
    Mono<Map<String, Object>> execute(Map<String, Object> context);
    Flux<String> stream(Map<String, Object> context);
}