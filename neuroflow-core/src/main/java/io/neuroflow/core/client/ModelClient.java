package io.neuroflow.core.client;
import io.neuroflow.core.model.ModelRequest;
import io.neuroflow.core.model.ModelResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * Created with IntelliJ IDEA.
 *
 * @Author: cc
 * @Date: 2025/06/21/20:58
 **/
public interface ModelClient {
    Mono<ModelResponse> call(ModelRequest request);
    Flux<String> stream(ModelRequest request);
}