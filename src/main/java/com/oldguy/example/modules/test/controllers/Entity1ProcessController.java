package com.oldguy.example.modules.test.controllers;

import com.oldguy.example.modules.common.utils.FormValidateUtils;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.test.dao.entities.Entity1Process;
import com.oldguy.example.modules.test.dao.jpas.Entity1ProcessMapper;
import com.oldguy.example.modules.test.service.Entity1ProcessService;
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
 * @date 2019/1/18
 */
@RestController
@RequestMapping("Entity1Process")
public class Entity1ProcessController {

    @Autowired
    private Entity1ProcessService entity1ProcessService;
    @Autowired
    private Entity1ProcessMapper entity1ProcessMapper;
    @Autowired
    private UserTaskService userTaskService;


    @GetMapping("all")
    public List<Entity1Process> getList(Integer status) {
        return entity1ProcessMapper.findAllByStatus(status);
    }

    @PostMapping("step2/complete")
    public Object completeStep2Task(TaskForm form) {
        FormValidateUtils.validate(form);
        // 完成任务
        TaskEntityInfo info = entity1ProcessService.completeStep2Task(form);
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
        String processInstanceId = entity1ProcessService.openProcessInstance();
        // 更新审核状态
        userTaskService.updateAuditStatus(processInstanceId);
        return HttpJsonUtils.OK;
    }
}
