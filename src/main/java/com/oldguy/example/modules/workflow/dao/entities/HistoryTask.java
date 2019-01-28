package com.oldguy.example.modules.workflow.dao.entities;


import com.oldguy.example.modules.common.dao.entities.BaseEntity;

import javax.persistence.Entity;

/**
 * @author huangrenhao
 * @date 2018/12/27
 */
@Entity
public class HistoryTask extends BaseEntity {

    /**
     *  任务ID
     */
    private String taskId;

    /**
     *  任务名称
     */
    private String taskName;

    /**
     *  业务Key
     */
    private String businessKey;

    /**
     *  流程实例ID
     */
    private String processInstanceId;

    /**
     *  流程定义Key
     */
    private String processDefineKey;

    /**
     *  流程定义ID
     */
    private String processDefineId;

    /**
     *  批注
     */
    private String comment;

    /**
     *  任务标示
     */
    private String flowFlag;

    /**
     *  创建人ID
     */
    private String creatorId;

    /**
     *  创建人名称
     */
    private String creatorName;

    public String getFlowFlag() {
        return flowFlag;
    }

    public void setFlowFlag(String flowFlag) {
        this.flowFlag = flowFlag;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefineKey() {
        return processDefineKey;
    }

    public void setProcessDefineKey(String processDefineKey) {
        this.processDefineKey = processDefineKey;
    }

    public String getProcessDefineId() {
        return processDefineId;
    }

    public void setProcessDefineId(String processDefineId) {
        this.processDefineId = processDefineId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
