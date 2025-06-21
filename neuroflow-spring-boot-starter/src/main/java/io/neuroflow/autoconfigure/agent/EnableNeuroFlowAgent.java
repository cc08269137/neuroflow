package io.neuroflow.autoconfigure.agent;

import io.neuroflow.autoconfigure.EnableNeuroFlow;
import io.neuroflow.autoconfigure.agent.NeuroFlowAgentAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 NeuroFlow 智能体和工作流功能的注解
 *
 * <p>扩展了 {@link EnableNeuroFlow} 并添加了工作流引擎支持
 *
 * <p>使用示例：
 * <pre>{@code
 * @SpringBootApplication
 * @EnableNeuroFlowAgent
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
@EnableNeuroFlow
@Import(NeuroFlowAgentAutoConfiguration.class)
public @interface EnableNeuroFlowAgent {
    /**
     * 工作流自动重新加载间隔（秒）
     * @return 默认 30 秒
     */
    int workflowReloadInterval() default 30;

    /**
     * 是否启用工作流可视化端点
     * @return 默认 true
     */
    boolean exposeWorkflowEndpoint() default true;
}