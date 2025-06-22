package io.neuroflow.core.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FunctionRegistry {
    private static final Logger log = LoggerFactory.getLogger(FunctionRegistry.class);
    private final Map<String, FunctionDescriptor> functions = new HashMap<>();

    public void registerFunction(String name, FunctionDescriptor descriptor) {
        log.info("[FunctionRegistry] 注册函数: {}，描述: {}，参数数量: {}", 
                name, descriptor.getDescription(), descriptor.getParameters().size());
        functions.put(name, descriptor);
        log.debug("[FunctionRegistry] 函数 {} 注册成功，目标: {}，方法: {}", 
                name, descriptor.getTarget().getClass().getSimpleName(), descriptor.getMethod().getName());
    }

    public Object call(String name, Map<String, Object> args) {
        log.info("[FunctionRegistry] 调用函数: {}，参数: {}", name, args);
        FunctionDescriptor descriptor = functions.get(name);
        if (descriptor == null) {
            log.error("[FunctionRegistry] 函数未找到: {}", name);
            throw new IllegalArgumentException("Function not found: " + name);
        }

        try {
            Object[] params = prepareArguments(descriptor, args);
            log.debug("[FunctionRegistry] 函数 {} 参数准备完成: {}", name, params);
            Object result = descriptor.getMethod().invoke(descriptor.getTarget(), params);
            log.info("[FunctionRegistry] 函数 {} 调用成功，结果: {}", name, result);
            return result;
        } catch (Exception e) {
            log.error("[FunctionRegistry] 函数 {} 调用失败", name, e);
            throw new RuntimeException("Failed to execute function: " + name, e);
        }
    }

    private Object[] prepareArguments(FunctionDescriptor descriptor, Map<String, Object> args) {
        log.debug("[FunctionRegistry] 准备函数参数，函数: {}，输入参数: {}", descriptor.getName(), args);
        Object[] params = new Object[descriptor.getParameters().size()];
        for (int i = 0; i < descriptor.getParameters().size(); i++) {
            FunctionParameter param = descriptor.getParameters().get(i);
            Object value = args.get(param.getName());
            if (value == null && param.isRequired()) {
                log.error("[FunctionRegistry] 缺少必需参数: {}，函数: {}", param.getName(), descriptor.getName());
                throw new IllegalArgumentException("Missing required parameter: " + param.getName());
            }
            if (value == null && param.getDefaultValue() != null) {
                value = param.getDefaultValue();
                log.debug("[FunctionRegistry] 使用默认值: {} = {}，函数: {}", param.getName(), value, descriptor.getName());
            }
            params[i] = value;
        }
        log.debug("[FunctionRegistry] 参数准备完成: {}", params);
        return params;
    }
    
    /**
     * 获取所有注册的函数名称
     */
    public java.util.Set<String> getRegisteredFunctions() {
        return functions.keySet();
    }
    
    /**
     * 检查函数是否存在
     */
    public boolean hasFunction(String name) {
        return functions.containsKey(name);
    }
    
    /**
     * 获取函数描述
     */
    public FunctionDescriptor getFunctionDescriptor(String name) {
        return functions.get(name);
    }
}