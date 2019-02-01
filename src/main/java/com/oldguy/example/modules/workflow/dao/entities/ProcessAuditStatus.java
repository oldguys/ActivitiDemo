package com.oldguy.example.modules.workflow.dao.entities;

import com.oldguy.example.modules.common.dao.entities.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

/**
 * @author huangrenhao
 * @date 2019/1/23
 */
@Entity
public class ProcessAuditStatus extends BaseEntity {

    private String processDefinitionKey;

    private String processDefinitionId;

    private String userTask;

    private String auditCode;

    private String auditMessage;

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getUserTask() {
        return userTask;
    }

    public void setUserTask(String userTask) {
        this.userTask = userTask;
    }

    public String getAuditCode() {
        return auditCode;
    }

    public void setAuditCode(String auditCode) {
        this.auditCode = auditCode;
    }

    public String getAuditMessage() {
        return auditMessage;
    }

    public void setAuditMessage(String auditMessage) {
        this.auditMessage = auditMessage;
    }
}
