package io.neuroflow.core.function;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AIFunction {
    String name();
    String description() default "";
    int timeout() default 3000;
} 