package io.neuroflow.core.function;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FunctionRegistry {
    private final Map<String, FunctionDescriptor> functions = new HashMap<>();

    public void registerFunction(String name, FunctionDescriptor descriptor) {
        functions.put(name, descriptor);
    }

    public Object call(String name, Map<String, Object> args) {
        FunctionDescriptor descriptor = functions.get(name);
        if (descriptor == null) {
            throw new IllegalArgumentException("Function not found: " + name);
        }

        try {
            Object[] params = prepareArguments(descriptor, args);
            return descriptor.getMethod().invoke(descriptor.getTarget(), params);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute function: " + name, e);
        }
    }

    private Object[] prepareArguments(FunctionDescriptor descriptor, Map<String, Object> args) {
        Object[] params = new Object[descriptor.getParameters().size()];
        for (int i = 0; i < descriptor.getParameters().size(); i++) {
            FunctionParameter param = descriptor.getParameters().get(i);
            Object value = args.get(param.getName());
            if (value == null && param.isRequired()) {
                throw new IllegalArgumentException("Missing required parameter: " + param.getName());
            }
            params[i] = value;
        }
        return params;
    }
}