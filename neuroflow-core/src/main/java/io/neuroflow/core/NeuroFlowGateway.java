package io.neuroflow.core;

import io.neuroflow.core.client.ModelClient;
import io.neuroflow.core.model.ModelRequest;
import io.neuroflow.core.model.ModelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NeuroFlowGateway {
    private static final Logger log = LoggerFactory.getLogger(NeuroFlowGateway.class);

    private final Map<String, ModelClient> modelClients = new HashMap<>();
    private final List<String> modelPriority;

    public NeuroFlowGateway(Map<String, ModelClient> modelClients, List<String> modelPriority) {
        this.modelClients.putAll(modelClients);
        this.modelPriority = modelPriority;
        log.info("NeuroFlowGateway initialized with {} models", modelClients.size());
    }

    public Mono<ModelResponse> execute(ModelRequest request) {
        String model = selectModel(request.getModel());
        log.info("[NeuroFlowGateway] 执行请求，模型: {}，原始请求模型: {}", model, request.getModel());
        log.debug("[NeuroFlowGateway] 请求详情: {}", request);
        return modelClients.get(model).call(request)
                .doOnSuccess(resp -> log.info("[NeuroFlowGateway] 请求执行成功，模型: {}，响应ID: {}", model, resp.getId()))
                .doOnError(e -> log.error("[NeuroFlowGateway] 请求执行失败，模型: {}", model, e));
    }

    public Flux<String> stream(ModelRequest request) {
        String model = selectModel(request.getModel());
        log.info("[NeuroFlowGateway] 流式请求，模型: {}，原始请求模型: {}", model, request.getModel());
        log.debug("[NeuroFlowGateway] 流式请求详情: {}", request);
        return modelClients.get(model).stream(request)
                .doOnNext(token -> log.debug("[NeuroFlowGateway] 流式响应: {} -> {}", model, token))
                .doOnComplete(() -> log.info("[NeuroFlowGateway] 流式请求完成，模型: {}", model))
                .doOnError(e -> log.error("[NeuroFlowGateway] 流式请求失败，模型: {}", model, e));
    }

    private String selectModel(String preferredModel) {
        log.debug("[NeuroFlowGateway] 选择模型，偏好模型: {}，可用模型: {}", preferredModel, modelClients.keySet());
        
        if (preferredModel != null && modelClients.containsKey(preferredModel)) {
            log.info("[NeuroFlowGateway] 使用偏好模型: {}", preferredModel);
            return preferredModel;
        }

        for (String model : modelPriority) {
            if (modelClients.containsKey(model)) {
                log.info("[NeuroFlowGateway] 按优先级选择模型: {} (偏好模型 {} 不可用)", model, preferredModel);
                return model;
            }
        }

        log.warn("[NeuroFlowGateway] 没有可用模型，使用第一个可用模型");
        String fallbackModel = modelClients.keySet().iterator().next();
        log.info("[NeuroFlowGateway] 使用回退模型: {}", fallbackModel);
        return fallbackModel;
    }
}