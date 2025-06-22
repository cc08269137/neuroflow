package io.neuroflow.example;

import io.neuroflow.core.function.AIFunction;
import io.neuroflow.core.function.DefaultValue;
import io.neuroflow.core.function.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * AI 函数示例
 * 展示如何使用 @AIFunction 注解注册 AI 可调用函数
 */
@Service
public class ExampleAIFunctions {

    /**
     * 天气查询函数
     */
    @AIFunction(name = "get_weather", description = "获取指定城市的天气信息")
    public WeatherData getWeather(
            @Param(name = "city", description = "城市名称") String city,
            @DefaultValue("metric") @Param(name = "unit", description = "温度单位") String unit) {
        
        // 模拟天气 API 调用
        WeatherData weather = new WeatherData();
        weather.setCity(city);
        weather.setTemperature(25.0);
        weather.setUnit(unit);
        weather.setCondition("晴天");
        weather.setHumidity(60);
        weather.setTimestamp(LocalDateTime.now());
        
        return weather;
    }

    /**
     * 时间查询函数
     */
    @AIFunction(name = "get_current_time", description = "获取当前时间")
    public Map<String, Object> getCurrentTime(
            @DefaultValue("Asia/Shanghai") @Param(name = "timezone", description = "时区") String timezone) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("timezone", timezone);
        result.put("current_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.put("timestamp", System.currentTimeMillis());
        
        return result;
    }

    /**
     * 计算器函数
     */
    @AIFunction(name = "calculate", description = "执行数学计算")
    public Map<String, Object> calculate(
            @Param(name = "expression", description = "数学表达式") String expression,
            @DefaultValue("0") @Param(name = "precision", description = "精度") int precision) {
        
        // 这里应该实现表达式解析和计算
        // 简化示例
        Map<String, Object> result = new HashMap<>();
        result.put("expression", expression);
        result.put("result", "计算结果");
        result.put("precision", precision);
        
        return result;
    }

    /**
     * 文本处理函数
     */
    @AIFunction(name = "process_text", description = "处理文本内容")
    public Map<String, Object> processText(
            @Param(name = "text", description = "输入文本") String text,
            @DefaultValue("summarize") @Param(name = "operation", description = "操作类型") String operation) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("original_text", text);
        result.put("operation", operation);
        result.put("processed_text", "处理后的文本");
        result.put("word_count", text.split("\\s+").length);
        
        return result;
    }

    /**
     * 天气数据类
     */
    public static class WeatherData {
        private String city;
        private double temperature;
        private String unit;
        private String condition;
        private int humidity;
        private LocalDateTime timestamp;

        // Getters and Setters
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        public int getHumidity() { return humidity; }
        public void setHumidity(int humidity) { this.humidity = humidity; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return String.format("WeatherData{city='%s', temperature=%.1f%s, condition='%s', humidity=%d%%}", 
                    city, temperature, unit, condition, humidity);
        }
    }
} 