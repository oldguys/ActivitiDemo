package com.oldguy.example.modules.workflow.dto;

import java.util.List;

/**
 *  任务实体
 * @author huangrenhao
 * @date 2018/12/5
 */
public class WorkEntityInfo {

    /**
     *  任务信息
     */
    private TaskEntityInfo taskEntityInfo;

    /**
     *  任务批注
     */
    private List<TaskComment> taskComments;

    /**
     *  任务按钮
     */
    private List<WorkBtn> workBtnList;

    /**
     *  数据对象
     */
    private Object target;

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public TaskEntityInfo getTaskEntityInfo() {
        return taskEntityInfo;
    }

    public void setTaskEntityInfo(TaskEntityInfo taskEntityInfo) {
        this.taskEntityInfo = taskEntityInfo;
    }

    public List<TaskComment> getTaskComments() {
        return taskComments;
    }

    public void setTaskComments(List<TaskComment> taskComments) {
        this.taskComments = taskComments;
    }

    public List<WorkBtn> getWorkBtnList() {
        return workBtnList;
    }

    public void setWorkBtnList(List<WorkBtn> workBtnList) {
        this.workBtnList = workBtnList;
    }
}
