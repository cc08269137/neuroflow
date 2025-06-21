package io.neuroflow.core.function;


import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.List;

@Getter
@Setter
public class FunctionDescriptor {
    private String name;
    private String description;
    private Method method;
    private Object target;
    private List<FunctionParameter> parameters;
}