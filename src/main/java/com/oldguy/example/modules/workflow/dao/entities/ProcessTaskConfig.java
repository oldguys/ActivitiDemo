package com.oldguy.example.modules.workflow.dao.entities;

import com.oldguy.example.modules.common.dao.entities.BaseEntity;

import javax.persistence.Entity;

/**
 * @Date: 2019/1/26 0026
 * @Author: ren
 * @Description:
 */
@Entity
public class ProcessTaskConfig extends BaseEntity{

    /**
     *  流程定义ID
     */
    private String processDefinitionId;

    /**
     *  流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 流程判定标识
     */
    private String flowFlag;

    /**
     *  连线ID
     */
    private String flowId;

    /**
     *  显示按钮名称
     */
    private String btn;

    /**
     *  相应后台链接
     */
    private String url;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
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

    public String getFlowFlag() {
        return flowFlag;
    }

    public void setFlowFlag(String flowFlag) {
        this.flowFlag = flowFlag;
    }

    public String getBtn() {
        return btn;
    }

    public void setBtn(String btn) {
        this.btn = btn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
