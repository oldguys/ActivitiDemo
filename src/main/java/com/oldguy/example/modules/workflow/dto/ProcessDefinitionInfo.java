package com.oldguy.example.modules.workflow.dto;

import com.oldguy.example.modules.workflow.dao.entities.ProcessAuditStatus;
import com.oldguy.example.modules.workflow.dao.entities.ProcessTaskConfig;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author huangrenhao
 * @date 2019/1/23
 */
public class ProcessDefinitionInfo {

    private String Id;

    private String key;

    private String name;

    private String deploymentId;

    private Integer version;

    private List<TaskElement> elements = Collections.emptyList();

    private List<TaskFlow> taskFlowList = Collections.emptyList();

    private Map<String, ProcessAuditStatus> elementValueMap = Collections.emptyMap();

    private Map<String, ProcessTaskConfig> taskConfigMap = Collections.emptyMap();

    public ProcessDefinitionInfo(String id, String key, String name, String deploymentId, Integer version) {
        Id = id;
        this.key = key;
        this.name = name;
        this.deploymentId = deploymentId;
        this.version = version;
    }

    public void setTaskConfigMap(Map<String, ProcessTaskConfig> taskConfigMap) {
        this.taskConfigMap = taskConfigMap;
    }

    public void setTaskFlowList(List<TaskFlow> taskFlowList) {
        this.taskFlowList = taskFlowList;
    }

    public void setElementValueMap(Map<String, ProcessAuditStatus> elementValueMap) {
        this.elementValueMap = elementValueMap;
    }

    public void setElements(List<TaskElement> elements) {
        this.elements = elements;
    }

    @Data
    public static class TaskElement {

        private String id;

        private String name;

        public TaskElement(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<TaskElement> getElements() {
        return elements;
    }

    public List<TaskFlow> getTaskFlowList() {
        return taskFlowList;
    }

    public Map<String, ProcessAuditStatus> getElementValueMap() {
        return elementValueMap;
    }

    public Map<String, ProcessTaskConfig> getTaskConfigMap() {
        return taskConfigMap;
    }
}
