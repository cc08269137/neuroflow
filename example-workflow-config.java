package io.neuroflow.example;

import io.neuroflow.agent.workflow.DSL;
import io.neuroflow.agent.workflow.Workflow;
import io.neuroflow.core.NeuroFlowGateway;
import io.neuroflow.core.function.FunctionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 工作流配置示例
 * 展示如何使用 DSL 创建工作流
 */
@Configuration
public class ExampleWorkflowConfig {

    /**
     * 天气查询工作流示例
     */
    @Bean
    public Workflow weatherWorkflow(NeuroFlowGateway gateway, FunctionRegistry registry) {
        return DSL.workflow("weather-report", gateway)
                .llmStep("extract_city", "从文本提取城市名称: {{input}}", "gpt-3.5-turbo")
                .functionStep("fetch_weather", "get_weather", registry)
                .llmStep("generate_response", "根据天气数据生成友好报告: {{fetch_weather.output}}", "gpt-4")
                .build();
    }

    /**
     * 智能客服工作流示例
     */
    @Bean
    public Workflow customerServiceWorkflow(NeuroFlowGateway gateway, FunctionRegistry registry) {
        return DSL.workflow("customer-service", gateway)
                .llmStep("intent_classification", "识别用户意图: {{user_query}}", "gpt-3.5-turbo")
                .conditionalStep("route_intent", 
                        context -> "order".equals(context.get("intent")),
                        DSL.llmStep("order_query", "查询订单信息", "gpt-3.5-turbo", gateway),
                        DSL.llmStep("general_query", "处理一般查询", "gpt-3.5-turbo", gateway))
                .llmStep("generate_response", "生成最终回复", "gpt-4")
                .build();
    }

    /**
     * 数据分析工作流示例
     */
    @Bean
    public Workflow dataAnalysisWorkflow(NeuroFlowGateway gateway, FunctionRegistry registry) {
        return DSL.workflow("data-analysis", gateway)
                .llmStep("parse_query", "解析数据分析需求: {{query}}", "gpt-3.5-turbo")
                .functionStep("execute_sql", "text2sql", registry)
                .llmStep("analyze_results", "分析数据结果: {{execute_sql.output}}", "gpt-4")
                .llmStep("generate_report", "生成分析报告", "gpt-4")
                .build();
    }

    /**
     * 带错误处理的工作流示例
     */
    @Bean
    public Workflow robustWorkflow(NeuroFlowGateway gateway, FunctionRegistry registry) {
        return DSL.workflow("robust-process", gateway)
                .step(DSL.errorHandlingStep("main_process",
                        DSL.functionStep("critical_function", "critical_operation", registry, gateway),
                        error -> DSL.llmStep("fallback", "生成服务不可用的友好提示", "gpt-3.5-turbo", gateway)))
                .build();
    }
} 