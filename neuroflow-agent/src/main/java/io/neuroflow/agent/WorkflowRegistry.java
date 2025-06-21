package io.neuroflow.agent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 工作流注册中心，负责工作流的存储、检索和管理
 */
@Component
public class WorkflowRegistry {
    private static final Logger log = LoggerFactory.getLogger(WorkflowRegistry.class);

    private final Map<String, Workflow> workflowMap = new ConcurrentHashMap<>();
    private final Map<String, WorkflowMetadata> metadataMap = new ConcurrentHashMap<>();

    /**
     * 注册工作流
     * @param workflow 工作流实例
     */
    public void registerWorkflow(Workflow workflow) {
        String id = workflow.getId();
        if (workflowMap.containsKey(id)) {
            log.warn("Overwriting existing workflow: {}", id);
        }

        workflowMap.put(id, workflow);
        metadataMap.put(id, new WorkflowMetadata(
                id,
                workflow.getClass().getSimpleName(),
                System.currentTimeMillis()
        ));

        log.info("Registered workflow: {}", id);
    }

    /**
     * 获取工作流
     * @param id 工作流ID
     * @return 工作流实例，如果不存在返回null
     */
    public Workflow getWorkflow(String id) {
        return workflowMap.get(id);
    }

    /**
     * 获取所有工作流
     * @return 所有注册的工作流列表
     */
    public Collection<Workflow> getAllWorkflows() {
        return Collections.unmodifiableCollection(workflowMap.values());
    }

    /**
     * 获取工作流元数据
     * @param id 工作流ID
     * @return 工作流元数据
     */
    public WorkflowMetadata getWorkflowMetadata(String id) {
        return metadataMap.get(id);
    }

    /**
     * 获取所有工作流元数据
     * @return 所有工作流元数据列表
     */
    public List<WorkflowMetadata> getAllWorkflowMetadata() {
        return new ArrayList<>(metadataMap.values());
    }

    /**
     * 取消注册工作流
     * @param id 工作流ID
     */
    public void unregisterWorkflow(String id) {
        workflowMap.remove(id);
        metadataMap.remove(id);
        log.info("Unregistered workflow: {}", id);
    }

    /**
     * 重新加载工作流
     * @param workflow 更新后的工作流
     */
    public void reloadWorkflow(Workflow workflow) {
        String id = workflow.getId();
        if (!workflowMap.containsKey(id)) {
            log.warn("Cannot reload unregistered workflow: {}", id);
            return;
        }

        workflowMap.put(id, workflow);
        metadataMap.get(id).setLastReloaded(System.currentTimeMillis());
        log.info("Reloaded workflow: {}", id);
    }

    /**
     * 工作流元数据类
     */
    public static class WorkflowMetadata {
        private final String id;
        private final String type;
        private long lastReloaded;
        private String description;

        public WorkflowMetadata(String id, String type, long lastReloaded) {
            this.id = id;
            this.type = type;
            this.lastReloaded = lastReloaded;
        }

        // Getters and Setters
        public String getId() { return id; }
        public String getType() { return type; }
        public long getLastReloaded() { return lastReloaded; }
        public void setLastReloaded(long lastReloaded) { this.lastReloaded = lastReloaded; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}