package com.oldguy.example.modules.test.service;

import com.oldguy.example.modules.common.services.BaseService;
import com.oldguy.example.modules.test.dao.entities.Entity6Process;
import com.oldguy.example.modules.test.dao.jpas.Entity6ProcessMapper;
import com.oldguy.example.modules.workflow.commands.AddMultiInstanceExecutionCmd;
import com.oldguy.example.modules.workflow.commands.DeleteMultiInstanceExecutionCmd;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: Entity5ProcessService
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/6 0006 上午 10:57
 **/
@Service
public class Entity6ProcessService extends BaseService<Entity6Process> {


    @Autowired
    private Entity6ProcessMapper entity6ProcessMapper;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ManagementService managementService;

    @Transactional(rollbackFor = Exception.class)
    public String openProcessInstance() {

        Entity6Process entity6Process = new Entity6Process();
        newInstance(entity6Process);
        entity6ProcessMapper.save(entity6Process);

        String key = Entity6Process.class.getSimpleName();

        Map<String, Object> data = new HashMap<>(1);


        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("2");
//        assigneeList.add("4");

        data.put("assigneeList", assigneeList);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, key + "." + entity6Process.getId(), data);

        System.out.println(processInstance.getId());


        return "";
    }

    public void addCountersigningTask(String taskId, List<String> assigneeList) {
        addCountersigningTask(taskId, assigneeList, null);
    }

    /**
     * 会签加签
     *
     * @param taskId
     * @param assigneeList
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCountersigningTask(String taskId, List<String> assigneeList, String assignee) {

        managementService.executeCommand(new AddMultiInstanceExecutionCmd(taskId, assigneeList, assignee));

    }

    @Transactional(rollbackFor = Exception.class)
    public void removeCountersigningTask(String taskId, List<String> assigneeList) {

        managementService.executeCommand(new DeleteMultiInstanceExecutionCmd(taskId, assigneeList));

    }
}
