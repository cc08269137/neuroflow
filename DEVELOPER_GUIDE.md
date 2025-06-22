# NeuroFlow 开发者使用指南

## 🚀 快速开始

### 1. 添加依赖

在你的 Spring Boot 项目中添加 NeuroFlow 依赖：

```xml
<dependency>
    <groupId>io.neuroflow</groupId>
    <artifactId>neuroflow-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置大模型

在 `application.yml` 中配置大模型信息：

```yaml
neuroflow:
  gateway:
    models:
      # OpenAI 配置
      openai:
        api-key: ${OPENAI_API_KEY}  # 从环境变量读取
        endpoint: https://api.openai.com/v1
      
      # 阿里云 Qwen 配置
      qwen:
        api-key: ${QWEN_API_KEY}  # 从环境变量读取
        endpoint: dashscope.aliyuncs.com
      
      # 本地 Ollama 配置
      ollama:
        base-url: http://localhost:11434
        model: llama3
    
    # 模型优先级（按顺序尝试）
    model-priority:
      - openai
      - qwen
      - ollama
    
    # 超时和重试配置
    timeout: 5000
    max-retries: 3

  # Agent 配置
  agent:
    enabled: true
    workflow-dir: classpath:/workflows
    auto-reload: true

  # 工具配置
  tools:
    text2sql:
      enabled: true
    ocr:
      enabled: false

  # 向量存储配置
  vector-store:
    type: redis
    redis:
      host: localhost
      port: 6379
      index-name: neuroflow-vectors
      password: ${REDIS_PASSWORD}

  # 日志配置
  logging:
    level: INFO
    format: json
    enable-metrics: true
```

### 3. 环境变量配置

创建 `.env` 文件或设置环境变量：

```bash
# OpenAI
export OPENAI_API_KEY=your_openai_api_key_here

# 阿里云 Qwen
export QWEN_API_KEY=your_qwen_api_key_here

# Redis（可选）
export REDIS_PASSWORD=your_redis_password_here
```

## 🤖 大模型输出位置

### 1. 模型响应结构

大模型的输出在 `ModelResponse` 类中：

```java
public class ModelResponse {
    private List<Choice> choices;  // 主要输出位置
    
    public static class Choice {
        private ChatMessage message;  // 实际内容
        private String finishReason;
    }
    
    public static class ChatMessage {
        private String role;     // "assistant"
        private String content;  // 模型生成的文本内容
    }
}
```

### 2. 获取模型输出

在 `LLMCallStep` 中，模型输出被提取并存储到上下文中：

```java
// 在 LLMCallStep.doExecute() 方法中
context.put(name + ".output", 
    resp.getChoices() != null && !resp.getChoices().isEmpty() 
        ? resp.getChoices().get(0).getMessage().getContent() 
        : null);
```

### 3. 流式输出

对于流式响应，每个 token 通过 `Flux<String>` 返回：

```java
// 在 LLMCallStep.doStream() 方法中
return gateway.stream(req)
    .doOnNext(token -> log.debug("流式响应: {}", token));
```

## 📝 创建 AI 函数

### 1. 使用 @AIFunction 注解

```java
@Component
public class WeatherService {
    
    @AIFunction(
        name = "get_weather",
        description = "获取指定城市的天气信息",
        parameters = {
            @Parameter(name = "city", description = "城市名称", required = true),
            @Parameter(name = "date", description = "日期（可选）", required = false)
        }
    )
    public String getWeather(String city, String date) {
        // 实现天气查询逻辑
        return "北京今天晴天，温度 20-25°C";
    }
}
```

### 2. 自动注册

AI 函数会在应用启动时自动注册到 `FunctionRegistry` 中。

## 🔄 创建工作流

### 1. 使用 DSL 构建器

```java
@Service
public class WorkflowService {
    
    public Workflow createAnalysisWorkflow() {
        return DSL.workflow("data_analysis")
            .llmStep("analyze", "分析以下数据：{{data}}", "gpt-4")
            .functionStep("process", "process_data", Map.of("input", "{{analyze.output}}"))
            .conditionalStep("decision", "{{process.result}}", "success", "error")
            .step("success", new SuccessStep("success"))
            .step("error", new ErrorHandlingStep("error"))
            .build();
    }
}
```

### 2. 工作流文件

在 `classpath:/workflows/` 目录下创建 YAML 文件：

```yaml
name: data_analysis
description: 数据分析工作流
steps:
  - name: analyze
    type: llm
    prompt: "分析以下数据：{{data}}"
    model: gpt-4
  
  - name: process
    type: function
    function: process_data
    parameters:
      input: "{{analyze.output}}"
  
  - name: decision
    type: conditional
    condition: "{{process.result}}"
    trueStep: success
    falseStep: error
```

## 🌐 REST API

### 1. 工作流管理

```bash
# 执行工作流
POST /api/workflows/execute
{
  "workflowName": "data_analysis",
  "context": {
    "data": "销售数据..."
  }
}

# 获取工作流列表
GET /api/workflows

# 获取工作流详情
GET /api/workflows/{name}
```

### 2. 函数管理

```bash
# 获取已注册函数
GET /api/functions

# 调用函数
POST /api/functions/call
{
  "functionName": "get_weather",
  "parameters": {
    "city": "北京"
  }
}
```

## 🔧 调试和监控

### 1. 日志配置

```yaml
logging:
  level:
    io.neuroflow: DEBUG  # 启用详细日志
    org.springframework: WARN
```

### 2. 关键日志点

- **模型调用**：`LLMCallStep` 记录请求和响应
- **函数执行**：`FunctionCallStep` 记录参数和结果
- **工作流执行**：`AbstractStep` 记录执行状态
- **错误处理**：所有步骤都有详细的错误日志

### 3. 监控指标

启用指标收集：

```yaml
neuroflow:
  logging:
    enable-metrics: true
```

## 🚨 常见问题

### 1. API Key 配置

确保正确设置环境变量：
```bash
echo $OPENAI_API_KEY  # 检查是否设置
```

### 2. 模型优先级

如果某个模型不可用，系统会自动尝试下一个：
```yaml
model-priority:
  - openai    # 优先使用 OpenAI
  - qwen      # 备用 Qwen
  - ollama    # 本地备用
```

### 3. 超时处理

调整超时时间：
```yaml
gateway:
  timeout: 10000  # 10秒超时
  max-retries: 3  # 重试3次
```

## 📚 示例项目

参考 `example.yml` 和测试类 `test-compilation.java` 了解完整的使用示例。 