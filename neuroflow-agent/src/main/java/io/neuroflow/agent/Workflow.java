package io.neuroflow.agent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface Workflow {
    String getId();
    Mono<Object> execute(Map<String, Object> inputs);
    Flux<String> stream(Map<String, Object> inputs);
}