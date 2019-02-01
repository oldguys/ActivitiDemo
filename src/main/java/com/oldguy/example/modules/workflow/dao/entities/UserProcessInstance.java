package com.oldguy.example.modules.workflow.dao.entities;

import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * @author huangrenhao
 * @date 2018/12/26
 */
@Data
public class UserProcessInstance {

    private String processInstanceId;

    private String businessKey;

    private String creatorId;

    private Date startTime;

    private Date endTime;


    private String processDefinitionKey;

    private String processDefinitionName;

    private String taskName;

    private String assignees;

    /**
     *  流程状态
     */
    private String active;

}
