package io.neuroflow.agent.workflow;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface Workflow {
    String getId();
    String getName();
    String getDescription();
    int getStepCount();

    Mono<Object> execute(Map<String, Object> inputs);
    Flux<String> stream(Map<String, Object> inputs);

    // 获取工作流元数据
    WorkflowMetadata getMetadata();

    // 获取工作流状态
    WorkflowStatus getStatus();
}