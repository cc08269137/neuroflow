package io.neuroflow.core.client.impl;


import io.neuroflow.core.client.ModelClient;
import io.neuroflow.core.model.ModelRequest;
import io.neuroflow.core.model.ModelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OpenAIClient implements ModelClient {
    private static final Logger log = LoggerFactory.getLogger(OpenAIClient.class);

    private final WebClient webClient;
    private final String apiKey;

    public OpenAIClient(String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public Mono<ModelResponse> call(ModelRequest request) {
        log.debug("Sending request to OpenAI: {}", request);
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ModelResponse.class)
                .doOnSuccess(res -> log.debug("Received OpenAI response"))
                .doOnError(e -> log.error("OpenAI request failed", e));
    }

    @Override
    public Flux<String> stream(ModelRequest request) {
        log.debug("Streaming request to OpenAI");
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnError(e -> log.error("OpenAI stream failed", e));
    }
}
