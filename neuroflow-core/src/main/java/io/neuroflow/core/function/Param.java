package io.neuroflow.core.function;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String name() default "";
    String description() default "";
    boolean required() default true;
}