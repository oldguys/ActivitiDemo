package com.oldguy.example.modules.test.service;

import com.oldguy.example.modules.sys.services.UserEntityService;
import com.oldguy.example.modules.test.dao.entities.Entity2Process;
import com.oldguy.example.modules.test.dao.entities.Entity3Process;
import com.oldguy.example.modules.test.dao.jpas.Entity3ProcessMapper;
import com.oldguy.example.modules.workflow.configs.WorkFlowConfiguration;
import com.oldguy.example.modules.workflow.dto.TaskEntityInfo;
import com.oldguy.example.modules.workflow.dto.TaskForm;
import com.oldguy.example.modules.workflow.service.ProcessService;
import com.oldguy.example.modules.workflow.service.UserTaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangrenhao
 * @date 2019/1/29
 */
@Service
public class Entity3ProcessService {

    @Autowired
    private Entity3ProcessMapper entity3ProcessMapper;
    @Autowired
    private ProcessService processService;
    @Autowired
    private UserTaskService userTaskService;

    @Transactional(rollbackFor = Exception.class)
    public TaskEntityInfo completeTask(TaskForm form) {

        TaskEntityInfo task = userTaskService.currentTaskInfo(form.getTaskId());

        Entity3Process instance = entity3ProcessMapper.findOne(task.getEntityId());
        instance.setConfirmDate(new Date());
        instance.setConfirmUserId(UserEntityService.getCurrentUserEntity().getUserId());
        instance.setConfirmUsername(UserEntityService.getCurrentUserEntity().getUsername());

        // 更新业务
        entity3ProcessMapper.update(instance);
        // 完成任务
        userTaskService.complete(form.getTaskId(), form.getComment(), form.getFlowFlag());

        return task;
    }

    @Transactional(rollbackFor = Exception.class)
    public String openProcessInstance(Collection<String> assigneeList) {

        Entity3Process instance = new Entity3Process();

        instance.setCreateTime(new Date());
        instance.setStatus(1);
        instance.setCreatorId(UserEntityService.getCurrentUserEntity().getUserId());
        instance.setCreatorName(UserEntityService.getCurrentUserEntity().getUsername());

        entity3ProcessMapper.save(instance);
        ProcessInstance processInstance = processService.openProcessInstance(Entity3Process.class.getSimpleName(), instance.getId(), UserEntityService.getCurrentUserEntity().getUserId());

        Map<String, Object> params = new HashMap(1);
        params.put(WorkFlowConfiguration.DEFAULT_USER_TASK_ASSIGNEE_LIST, assigneeList);

        userTaskService.completeTaskByProcessInstance(processInstance, params, Entity3Process.class.getSimpleName() + "." + instance.getId());
        return processInstance.getId();
    }

}
