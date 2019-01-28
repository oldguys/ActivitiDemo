package com.oldguy.example.modules.workflow.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author huangrenhao
 * @date 2018/12/3
 */
public class TaskEntityInfo implements Comparable<TaskEntityInfo> {

    private String id;

    private String taskName;

    private String taskDefinitionKey;

    private String processInstanceId;

    private String processDefinitionId;

    /**
     *  流程定义Key
     */
    private String processDefinitionKey;

    private String processDefinitionName;

    private Set<String> assigners;

    private Set<String> assignerNames;

    private Date createTime;

    private String businessKey;

    private Long entityId;

    /**
     * 历时
     */
    private long duration;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Set<String> getAssigners() {
        return assigners;
    }

    public void setAssigners(Set<String> assigners) {
        this.assigners = assigners;
    }

    public Set<String> getAssignerNames() {
        return assignerNames;
    }

    public void setAssignerNames(Set<String> assignerNames) {
        this.assignerNames = assignerNames;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }

    @Override
    public int compareTo(TaskEntityInfo o) {
        return (int) (o.createTime.getTime() - this.createTime.getTime());
    }
}
