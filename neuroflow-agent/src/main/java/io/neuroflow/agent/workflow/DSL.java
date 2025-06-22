package io.neuroflow.agent.workflow;

import io.neuroflow.core.NeuroFlowGateway;
import io.neuroflow.core.function.FunctionRegistry;

/**
 * 工作流 DSL 工具类
 * 提供链式 API 来创建工作流
 */
public class DSL {
    
    /**
     * 创建工作流
     * @param id 工作流ID
     * @param gateway AI网关
     * @return DSLWorkflow实例
     */
    public static DSLWorkflow workflow(String id, NeuroFlowGateway gateway) {
        return new DSLWorkflow(id, gateway);
    }
    
    /**
     * 创建LLM步骤
     * @param name 步骤名称
     * @param prompt 提示词
     * @param model 模型名称
     * @param gateway AI网关
     * @return LLMCallStep实例
     */
    public static LLMCallStep llmStep(String name, String prompt, String model, NeuroFlowGateway gateway) {
        return new LLMCallStep(name, prompt, model, gateway);
    }
    
    /**
     * 创建函数调用步骤
     * @param name 步骤名称
     * @param functionName 函数名称
     * @param registry 函数注册表
     * @param gateway AI网关
     * @return FunctionCallStep实例
     */
    public static FunctionCallStep functionStep(String name, String functionName, FunctionRegistry registry, NeuroFlowGateway gateway) {
        return new FunctionCallStep(name, functionName, registry, gateway);
    }
    
    /**
     * 创建条件步骤
     * @param name 步骤名称
     * @param condition 条件判断
     * @param ifStep 条件为真时的步骤
     * @param elseStep 条件为假时的步骤
     * @return ConditionalStep实例
     */
    public static ConditionalStep conditionalStep(String name, 
                                                  java.util.function.Predicate<java.util.Map<String, Object>> condition, 
                                                  Step ifStep, 
                                                  Step elseStep) {
        return new ConditionalStep(name, condition, ifStep, elseStep);
    }
    
    /**
     * 创建错误处理步骤
     * @param name 步骤名称
     * @param mainStep 主步骤
     * @param fallbackProvider 回退步骤提供者
     * @return ErrorHandlingStep实例
     */
    public static ErrorHandlingStep errorHandlingStep(String name, 
                                                      Step mainStep, 
                                                      java.util.function.Function<Throwable, Step> fallbackProvider) {
        return new ErrorHandlingStep(name, mainStep, fallbackProvider);
    }
} 