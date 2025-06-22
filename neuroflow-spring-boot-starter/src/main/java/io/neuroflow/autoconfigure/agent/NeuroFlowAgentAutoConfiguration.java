package io.neuroflow.autoconfigure.agent;

import io.neuroflow.agent.AgentOrchestrator;
import io.neuroflow.agent.WorkflowRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@AutoConfiguration
@EnableScheduling
@ConditionalOnBean(annotation = EnableNeuroFlowAgent.class)
public class NeuroFlowAgentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WorkflowRegistry workflowRegistry() {
        return new WorkflowRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public AgentOrchestrator agentOrchestrator(WorkflowRegistry workflowRegistry) {
        return new AgentOrchestrator(workflowRegistry);
    }

    @Bean
    public WorkflowEndpoint workflowEndpoint(WorkflowRegistry registry, AgentOrchestrator orchestrator) {
        return new WorkflowEndpoint(registry, orchestrator);
    }
}