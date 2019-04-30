package com.oldguy.example.modules.test.controllers;

import com.oldguy.example.modules.common.utils.FormValidateUtils;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.test.dao.entities.Entity2Process;
import com.oldguy.example.modules.test.dao.entities.Entity3Process;
import com.oldguy.example.modules.test.dao.jpas.Entity2ProcessMapper;
import com.oldguy.example.modules.test.dao.jpas.Entity3ProcessMapper;
import com.oldguy.example.modules.test.service.Entity2ProcessService;
import com.oldguy.example.modules.test.service.Entity3ProcessService;
import com.oldguy.example.modules.workflow.dto.TaskEntityInfo;
import com.oldguy.example.modules.workflow.dto.TaskForm;
import com.oldguy.example.modules.workflow.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangrenhao
 * @date 2019/1/29
 */
@RestController
@RequestMapping("Entity3Process")
public class Entity3ProcessController {

    @Autowired
    private Entity3ProcessService entity3ProcessService;
    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private Entity3ProcessMapper entity3ProcessMapper;


    @PostMapping("completeTask")
    public Object completeTask(TaskForm form) {
        FormValidateUtils.validate(form);
        // 完成任务
        TaskEntityInfo info = entity3ProcessService.completeTask(form);
        // 更新审核状态
        userTaskService.updateAuditStatus(info.getProcessInstanceId());

        return HttpJsonUtils.OK;
    }


    /**
     * 开启流程实例
     *
     * @return
     */
    @PostMapping("openProcessInstance")
    public Object openProcessInstance() {

        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("1");
        assigneeList.add("2");
        assigneeList.add("3");

        String processInstanceId = entity3ProcessService.openProcessInstance(assigneeList);
        // 更新审核状态
        userTaskService.updateAuditStatus(processInstanceId);
        return HttpJsonUtils.OK;
    }

    @GetMapping("all")
    public List<Entity3Process> getList(Integer status) {
        return entity3ProcessMapper.findAllByStatus(status);
    }

}
