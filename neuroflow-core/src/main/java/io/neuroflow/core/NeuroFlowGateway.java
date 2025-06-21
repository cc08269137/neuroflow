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
        log.info("Executing request on model: {}", model);
        return modelClients.get(model).call(request);
    }

    public Flux<String> stream(ModelRequest request) {
        String model = selectModel(request.getModel());
        log.info("Streaming request on model: {}", model);
        return modelClients.get(model).stream(request);
    }

    private String selectModel(String preferredModel) {
        if (preferredModel != null && modelClients.containsKey(preferredModel)) {
            return preferredModel;
        }

        for (String model : modelPriority) {
            if (modelClients.containsKey(model)) {
                return model;
            }
        }

        log.warn("No available model found, using first available");
        return modelClients.keySet().iterator().next();
    }
}