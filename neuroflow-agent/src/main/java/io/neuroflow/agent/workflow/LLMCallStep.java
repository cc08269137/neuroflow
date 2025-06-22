package io.neuroflow.agent.workflow;

import io.neuroflow.core.NeuroFlowGateway;
import io.neuroflow.core.model.ModelRequest;
import io.neuroflow.core.model.ModelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public class LLMCallStep extends AbstractStep {
    private static final Logger log = LoggerFactory.getLogger(LLMCallStep.class);
    private final String prompt;
    private final String model;
    private final NeuroFlowGateway gateway;

    public LLMCallStep(String name, String prompt, String model, NeuroFlowGateway gateway) {
        super(name);
        this.prompt = prompt;
        this.model = model;
        this.gateway = gateway;
    }

    @Override
    protected Mono<Map<String, Object>> doExecute(Map<String, Object> context) {
        String renderedPrompt = renderPrompt(prompt, context);
        log.info("[LLMCallStep] 执行: {}，模型: {}，prompt: {}", name, model, renderedPrompt);
        ModelRequest req = new ModelRequest();
        req.setModel(model);
        req.setPrompt(renderedPrompt);
        return gateway.execute(req)
                .map(resp -> {
                    log.info("[LLMCallStep] {} 响应: {}", name, resp);
                    context.put(name + ".output", resp.getChoices() != null && !resp.getChoices().isEmpty() ? resp.getChoices().get(0).getMessage().getContent() : null);
                    return createOutput(resp);
                })
                .doOnError(e -> log.error("[LLMCallStep] {} 执行异常", name, e));
    }

    @Override
    protected Flux<String> doStream(Map<String, Object> context) {
        String renderedPrompt = renderPrompt(prompt, context);
        log.info("[LLMCallStep] 流式执行: {}，模型: {}，prompt: {}", name, model, renderedPrompt);
        ModelRequest req = new ModelRequest();
        req.setModel(model);
        req.setPrompt(renderedPrompt);
        req.setStream(true);
        return gateway.stream(req)
                .doOnNext(token -> log.debug("[LLMCallStep] {} 流式响应: {}", name, token))
                .doOnError(e -> log.error("[LLMCallStep] {} 流式异常", name, e));
    }

    private String renderPrompt(String prompt, Map<String, Object> context) {
        String result = prompt;
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
        }
        return result;
    }
} 