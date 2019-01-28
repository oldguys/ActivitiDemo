package com.oldguy.example.modules.workflow.dao.entities;

import java.util.Date;
import java.util.Set;

/**
 * @author huangrenhao
 * @date 2018/12/26
 */
public class UserProcessInstance {

    /**
     *  流程定义Key
     */
    private String processDefinitionKey;

    private String id;

    private String businessKey;

    private String creatorId;

    private Date startTime;

    private Date endTime;

    private String taskName;

    private String assignee;

    private String confirmUserId;

    private String confirmUsername;

    private String processName;

    private String candidateUser;

    /**
     * 映射名
     *
     * @return
     */
    private String creatorName;

    private Set<String> assigneeNameSet;

    private Set<String> assigneeUserIds;

    private Object target;

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Set<String> getAssigneeNameSet() {
        return assigneeNameSet;
    }

    public void setAssigneeNameSet(Set<String> assigneeNameSet) {
        this.assigneeNameSet = assigneeNameSet;
    }

    public Set<String> getAssigneeUserIds() {
        return assigneeUserIds;
    }

    public void setAssigneeUserIds(Set<String> assigneeUserIds) {
        this.assigneeUserIds = assigneeUserIds;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getConfirmUserId() {
        return confirmUserId;
    }

    public void setConfirmUserId(String confirmUserId) {
        this.confirmUserId = confirmUserId;
    }

    public String getConfirmUsername() {
        return confirmUsername;
    }

    public void setConfirmUsername(String confirmUsername) {
        this.confirmUsername = confirmUsername;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getCandidateUser() {
        return candidateUser;
    }

    public void setCandidateUser(String candidateUser) {
        this.candidateUser = candidateUser;
    }
}
