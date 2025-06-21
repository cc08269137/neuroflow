package io.neuroflow.core.client.impl;

import io.neuroflow.core.client.ModelClient;
import io.neuroflow.core.model.ModelRequest;
import io.neuroflow.core.model.ModelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class QwenClient implements ModelClient {
    private static final Logger log = LoggerFactory.getLogger(QwenClient.class);

    private final WebClient webClient;
    private final String apiKey;

    public QwenClient(String apiKey, String endpoint) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(endpoint)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public Mono<ModelResponse> call(ModelRequest request) {
        log.debug("Sending request to Qwen: {}", request);
        return webClient.post()
                .uri("/api/v1/services/aigc/text-generation/generation")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ModelResponse.class)
                .doOnSuccess(res -> log.debug("Received Qwen response"))
                .doOnError(e -> log.error("Qwen request failed", e));
    }

    @Override
    public Flux<String> stream(ModelRequest request) {
        log.debug("Streaming request to Qwen");
        return webClient.post()
                .uri("/api/v1/services/aigc/text-generation/generation")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnError(e -> log.error("Qwen stream failed", e));
    }
}