package com.oldguy.example.modules.workflow.dto;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author huangrenhao
 * @date 2018/12/7
 */
public class TaskForm {

    /**
     * 任务ID
     */
    @NotBlank(message = "taskId 任务ID 不能为空!")
    private String taskId;

    /**
     * 批注
     */
    private String comment;

    /**
     * 流程标示
     */
    private String flowFlag;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFlowFlag() {
        return flowFlag;
    }

    public void setFlowFlag(String flowFlag) {
        this.flowFlag = flowFlag;
    }
}
