package io.neuroflow.test;

import io.neuroflow.agent.workflow.DSL;
import io.neuroflow.agent.workflow.Workflow;
import io.neuroflow.core.NeuroFlowGateway;
import io.neuroflow.core.function.FunctionRegistry;
import org.springframework.stereotype.Service;

/**
 * 编译测试类
 * 验证所有组件都能正常工作
 */
@Service
public class CompilationTest {

    /**
     * 测试工作流创建
     */
    public void testWorkflowCreation(NeuroFlowGateway gateway, FunctionRegistry registry) {
        // 测试 DSL 工作流创建
        Workflow workflow = DSL.workflow("test-workflow", gateway)
                .llmStep("step1", "测试步骤1", "gpt-3.5-turbo")
                .functionStep("step2", "test_function", registry)
                .build();
        
        System.out.println("工作流创建成功: " + workflow.getId());
    }

    /**
     * 测试条件步骤
     */
    public void testConditionalStep(NeuroFlowGateway gateway) {
        // 测试条件步骤创建
        var conditionStep = DSL.conditionalStep("test_condition",
                context -> true,
                DSL.llmStep("if_step", "条件为真", "gpt-3.5-turbo", gateway),
                DSL.llmStep("else_step", "条件为假", "gpt-3.5-turbo", gateway));
        
        System.out.println("条件步骤创建成功: " + conditionStep.getName());
    }

    /**
     * 测试错误处理步骤
     */
    public void testErrorHandlingStep(NeuroFlowGateway gateway, FunctionRegistry registry) {
        // 测试错误处理步骤创建
        var errorStep = DSL.errorHandlingStep("test_error_handling",
                DSL.functionStep("main_step", "test_function", registry, gateway),
                error -> DSL.llmStep("fallback", "错误回退", "gpt-3.5-turbo", gateway));
        
        System.out.println("错误处理步骤创建成功: " + errorStep.getName());
    }
} 