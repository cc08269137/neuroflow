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
        log.info("[OpenAIClient] 发送请求到 OpenAI，模型: {}", request.getModel());
        log.debug("[OpenAIClient] 请求详情: {}", request);
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ModelResponse.class)
                .doOnSuccess(res -> {
                    log.info("[OpenAIClient] 收到 OpenAI 响应，模型: {}，响应ID: {}", request.getModel(), res.getId());
                    if (res.getUsage() != null) {
                        log.debug("[OpenAIClient] Token 使用情况: 提示词={}, 完成={}, 总计={}", 
                                res.getUsage().getPromptTokens(), 
                                res.getUsage().getCompletionTokens(), 
                                res.getUsage().getTotalTokens());
                    }
                })
                .doOnError(e -> log.error("[OpenAIClient] OpenAI 请求失败，模型: {}", request.getModel(), e));
    }

    @Override
    public Flux<String> stream(ModelRequest request) {
        log.info("[OpenAIClient] 发送流式请求到 OpenAI，模型: {}", request.getModel());
        log.debug("[OpenAIClient] 流式请求详情: {}", request);
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(chunk -> log.debug("[OpenAIClient] 流式响应块: {}", chunk))
                .doOnComplete(() -> log.info("[OpenAIClient] OpenAI 流式请求完成，模型: {}", request.getModel()))
                .doOnError(e -> log.error("[OpenAIClient] OpenAI 流式请求失败，模型: {}", request.getModel(), e));
    }
}
