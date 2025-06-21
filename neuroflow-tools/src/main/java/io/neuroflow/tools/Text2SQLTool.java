package io.neuroflow.tools;

import io.neuroflow.core.NeuroFlowGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

public class Text2SQLTool {
    private static final Logger log = LoggerFactory.getLogger(Text2SQLTool.class);

    private final NeuroFlowGateway gateway;
    private final JdbcTemplate jdbcTemplate;

    public Text2SQLTool(NeuroFlowGateway gateway, JdbcTemplate jdbcTemplate) {
        this.gateway = gateway;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object execute(String naturalLanguageQuery, Map<String, Object> params) {
        log.info("Translating natural language to SQL: {}", naturalLanguageQuery);

        // 这里简化实现，实际应用中应使用LLM进行转换
        String sql = translateToSQL(naturalLanguageQuery);

        log.debug("Generated SQL: {}", sql);
        return jdbcTemplate.queryForList(sql, params);
    }

    private String translateToSQL(String naturalLanguageQuery) {
        // 在实际应用中，这里应该调用LLM进行自然语言到SQL的转换
        // 这里仅做简单演示
        if (naturalLanguageQuery.contains("用户")) {
            return "SELECT * FROM users WHERE name = :name";
        } else if (naturalLanguageQuery.contains("订单")) {
            return "SELECT * FROM orders WHERE status = :status";
        }
        return "SELECT * FROM " + naturalLanguageQuery.replace(" ", "_");
    }
}
