package io.neuroflow.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 NeuroFlow 核心功能的注解
 *
 * <p>在 Spring Boot 主类上添加此注解以激活以下功能：
 * <ul>
 *   <li>AI 网关（NeuroFlowGateway）</li>
 *   <li>函数注册表（FunctionRegistry）</li>
 *   <li>模型客户端（OpenAIClient, QwenClient 等）</li>
 *   <li>自动配置系统</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * @SpringBootApplication
 * @EnableNeuroFlow
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NeuroFlowAutoConfiguration.class)
public @interface EnableNeuroFlow {
    /**
     * 是否启用模型路由功能
     * @return 默认 true
     */
    boolean enableModelRouting() default true;

    /**
     * 是否自动扫描 AI 函数
     * @return 默认 true
     */
    boolean scanAIFunctions() default true;

    /**
     * 要扫描的包路径（用于 AI 函数）
     * @return 包路径数组
     */
    String[] basePackages() default {};
}