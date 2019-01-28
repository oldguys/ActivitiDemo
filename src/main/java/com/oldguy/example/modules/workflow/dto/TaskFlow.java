package com.oldguy.example.modules.workflow.dto;

import lombok.Data;

/**
 * @Date: 2019/1/26 0026
 * @Author: ren
 * @Description:
 */
@Data
public class TaskFlow {

    private String id;

    private String name;

    private String expression;

    private String sourceTask;

    private String targetTask;

    public TaskFlow(String id, String name, String expression, String sourceTask, String targetTask) {
        this.id = id;
        this.name = name;
        this.expression = expression;
        this.sourceTask = sourceTask;
        this.targetTask = targetTask;
    }
}
