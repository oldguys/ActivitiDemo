package com.oldguy.example.modules.test.service;

import com.oldguy.example.modules.sys.services.UserEntityService;
import com.oldguy.example.modules.test.dao.entities.Entity1Process;
import com.oldguy.example.modules.test.dao.jpas.Entity1ProcessMapper;
import com.oldguy.example.modules.workflow.dto.TaskEntityInfo;
import com.oldguy.example.modules.workflow.dto.TaskForm;
import com.oldguy.example.modules.workflow.service.ProcessService;
import com.oldguy.example.modules.workflow.service.UserTaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author huangrenhao
 * @date 2019/1/18
 */
@Service
public class Entity1ProcessService {

    @Autowired
    private Entity1ProcessMapper entity1ProcessMapper;
    @Autowired
    private ProcessService processService;
    @Autowired
    private UserTaskService userTaskService;

    @Transactional(rollbackFor = Exception.class)
    public TaskEntityInfo completeStep2Task(TaskForm form) {

        TaskEntityInfo task = userTaskService.currentTaskInfo(form.getTaskId());

        Entity1Process entity1Process = entity1ProcessMapper.findOne(task.getEntityId());
        entity1Process.setConfirmDate(new Date());
        entity1Process.setConfirmUserId(UserEntityService.getCurrentUserEntity().getUserId());
        entity1Process.setConfirmUsername(UserEntityService.getCurrentUserEntity().getUsername());

        // 更新业务
        entity1ProcessMapper.update(entity1Process);
        // 完成任务
        userTaskService.complete(form.getTaskId(), form.getComment(), form.getFlowFlag());

        return task;
    }

    @Transactional(rollbackFor = Exception.class)
    public String openProcessInstance() {

        Entity1Process instance = new Entity1Process();

        instance.setCreateTime(new Date());
        instance.setStatus(1);
        instance.setCreatorId(UserEntityService.getCurrentUserEntity().getUserId());
        instance.setCreatorName(UserEntityService.getCurrentUserEntity().getUsername());

        entity1ProcessMapper.save(instance);
        ProcessInstance processInstance = processService.openProcessInstanceWithFirstCommit(Entity1Process.class.getSimpleName(), instance.getId(), UserEntityService.getCurrentUserEntity().getUserId());
        return processInstance.getId();
    }


}
