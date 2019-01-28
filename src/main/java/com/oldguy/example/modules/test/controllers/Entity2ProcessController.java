package com.oldguy.example.modules.test.controllers;

import com.oldguy.example.modules.common.utils.FormValidateUtils;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.test.dao.entities.Entity1Process;
import com.oldguy.example.modules.test.dao.entities.Entity2Process;
import com.oldguy.example.modules.test.dao.jpas.Entity2ProcessMapper;
import com.oldguy.example.modules.test.service.Entity2ProcessService;
import com.oldguy.example.modules.workflow.dto.TaskEntityInfo;
import com.oldguy.example.modules.workflow.dto.TaskForm;
import com.oldguy.example.modules.workflow.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author huangrenhao
 * @date 2019/1/23
 */
@RestController
@RequestMapping("Entity2Process")
public class Entity2ProcessController {

    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private Entity2ProcessMapper entity2ProcessMapper;
    @Autowired
    private Entity2ProcessService entity2ProcessService;

    @PostMapping("completeTask")
    public Object completeTask(TaskForm form) {
        FormValidateUtils.validate(form);
        // 完成任务
        TaskEntityInfo info = entity2ProcessService.completeTask(form);
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
        String processInstanceId = entity2ProcessService.openProcessInstance();
        // 更新审核状态
        userTaskService.updateAuditStatus(processInstanceId);
        return HttpJsonUtils.OK;
    }

    @GetMapping("all")
    public List<Entity2Process> getList(Integer status) {
        return entity2ProcessMapper.findAllByStatus(status);
    }

}
