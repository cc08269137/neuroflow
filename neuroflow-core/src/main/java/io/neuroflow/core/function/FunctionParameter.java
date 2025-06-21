package io.neuroflow.core.function;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class FunctionParameter {
    private final String name;
    private final Class<?> type;
    private final String description;
    private final boolean required;
    private final Object defaultValue;
    private final Class<? extends Annotation> annotationType;

    public FunctionParameter(String name, Class<?> type, String description,
                             boolean required, Object defaultValue,
                             Class<? extends Annotation> annotationType) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
        this.annotationType = annotationType;
    }

    // 从方法参数构建FunctionParameter
    public static FunctionParameter fromMethodParameter(Parameter parameter) {
        String name = parameter.getName();
        Class<?> type = parameter.getType();
        String description = "";
        boolean required = true;
        Object defaultValue = null;
        Class<? extends Annotation> annotationType = null;

        // 处理@Param注解
        if (parameter.isAnnotationPresent(Param.class)) {
            Param param = parameter.getAnnotation(Param.class);
            name = param.name().isEmpty() ? name : param.name();
            description = param.description();
            required = param.required();
            annotationType = Param.class;
        }

        // 处理@DefaultValue注解
        if (parameter.isAnnotationPresent(DefaultValue.class)) {
            DefaultValue dv = parameter.getAnnotation(DefaultValue.class);
            defaultValue = parseDefaultValue(dv.value(), type);
            required = false; // 有默认值就不是必须的
            if (annotationType == null) {
                annotationType = DefaultValue.class;
            }
        }

        return new FunctionParameter(name, type, description, required, defaultValue, annotationType);
    }

    private static Object parseDefaultValue(String value, Class<?> type) {
        if (type == String.class) {
            return value;
        } else if (type == Integer.class || type == int.class) {
            return Integer.parseInt(value);
        } else if (type == Long.class || type == long.class) {
            return Long.parseLong(value);
        } else if (type == Double.class || type == double.class) {
            return Double.parseDouble(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value; // 无法解析时返回字符串
    }

    // Getters
    public String getName() { return name; }
    public Class<?> getType() { return type; }
    public String getDescription() { return description; }
    public boolean isRequired() { return required; }
    public Object getDefaultValue() { return defaultValue; }
    public Class<? extends Annotation> getAnnotationType() { return annotationType; }

    @Override
    public String toString() {
        return "FunctionParameter{" +
                "name='" + name + '\'' +
                ", type=" + type.getSimpleName() +
                ", required=" + required +
                (defaultValue != null ? ", defaultValue=" + defaultValue : "") +
                '}';
    }
}