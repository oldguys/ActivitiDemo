package com.oldguy.example.modules.workflow.dto;

import com.oldguy.example.modules.workflow.dao.entities.HistoryTask;

/**
 * @author huangrenhao
 * @date 2019/1/22
 */
public class HistoryTaskInfo extends HistoryTask {

    private String processDefineName;

    public String getProcessDefineName() {
        return processDefineName;
    }

    public void setProcessDefineName(String processDefineName) {
        this.processDefineName = processDefineName;
    }
}

