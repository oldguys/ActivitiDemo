package com.oldguy.example.modules.test.service;

import com.oldguy.example.modules.sys.services.UserEntityService;
import com.oldguy.example.modules.test.dao.entities.Entity1Process;
import com.oldguy.example.modules.test.dao.entities.Entity2Process;
import com.oldguy.example.modules.test.dao.jpas.Entity1ProcessMapper;
import com.oldguy.example.modules.test.dao.jpas.Entity2ProcessMapper;
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
 * @date 2019/1/23
 */
@Service
public class Entity2ProcessService {

    @Autowired
    private Entity2ProcessMapper entity2ProcessMapper;
    @Autowired
    private ProcessService processService;
    @Autowired
    private UserTaskService userTaskService;

    @Transactional(rollbackFor = Exception.class)
    public TaskEntityInfo completeTask(TaskForm form) {

        TaskEntityInfo task = userTaskService.currentTaskInfo(form.getTaskId());

        Entity2Process instance = entity2ProcessMapper.findOne(task.getEntityId());
        instance.setConfirmDate(new Date());
        instance.setConfirmUserId(UserEntityService.getCurrentUserEntity().getUserId());
        instance.setConfirmUsername(UserEntityService.getCurrentUserEntity().getUsername());

        // 更新业务
        entity2ProcessMapper.update(instance);
        // 完成任务
        userTaskService.complete(form.getTaskId(), form.getComment(), form.getFlowFlag());

        return task;
    }

    @Transactional(rollbackFor = Exception.class)
    public String openProcessInstance() {

        Entity2Process instance = new Entity2Process();

        instance.setCreateTime(new Date());
        instance.setStatus(1);
        instance.setCreatorId(UserEntityService.getCurrentUserEntity().getUserId());
        instance.setCreatorName(UserEntityService.getCurrentUserEntity().getUsername());

        entity2ProcessMapper.save(instance);
        ProcessInstance processInstance = processService.openProcessInstanceWithFirstCommit(Entity2Process.class.getSimpleName(), instance.getId(), UserEntityService.getCurrentUserEntity().getUserId());
        return processInstance.getId();
    }

}
