package com.oldguy.example.modules.test.service;

import com.oldguy.example.modules.common.utils.SpringContextUtils;
import com.oldguy.example.modules.sys.services.UserEntityService;
import com.oldguy.example.modules.test.dao.entities.Entity1Process;
import com.oldguy.example.modules.test.dao.entities.Entity4Process;
import com.oldguy.example.modules.test.dao.jpas.Entity3ProcessMapper;
import com.oldguy.example.modules.test.dao.jpas.Entity4ProcessMapper;
import com.oldguy.example.modules.workflow.service.ProcessService;
import com.oldguy.example.modules.workflow.service.UserTaskService;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ClassName: Entity4ProcessService
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/4/29 0029 上午 10:10
 **/
@Service
public class Entity4ProcessService {


    @Autowired
    private Entity4ProcessMapper entity4ProcessMapper;
    @Autowired
    private ProcessService processService;
    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ProcessEngine processEngine;

    @Transactional(rollbackFor = Exception.class)
    public String openProcessInstance() {

        Entity4Process instance = new Entity4Process();

        instance.setCreateTime(new Date());
        instance.setStatus(1);
        instance.setCreatorId(UserEntityService.getCurrentUserEntity().getUserId());
        instance.setCreatorName(UserEntityService.getCurrentUserEntity().getUsername());

        entity4ProcessMapper.save(instance);
        ProcessInstance processInstance = processService.openProcessInstanceWithFirstCommit(Entity4Process.class.getSimpleName(), instance.getId(), UserEntityService.getCurrentUserEntity().getUserId());
        return processInstance.getId();
    }


    /**
     *  变更节点
     *
     *  描述：
     *   流程： 节点1 - 节点2 - 节点3
     *   需求： 完成节点1 时，根据 表单参数确定是否转换 节点2（普通节点） 为 会签节点
     * 注意：
     *      1. EventListener 存在于 进行时任务， 这时已经根据 Bpmn Model 生成了流程 任务，此时改动无效。
     *      2. 经过测试, 会签任务生成的时，会把 入参 Map<"AssingeeList",List<"assignee">> 转换 成为多个任务。
     *      在 EventListener 中 assignee 已经被赋值。
     * 实现原理：
     *      1. 在完成 节点1 任务前，获取到Bpmn Model > Process > FlowElement（UseTask）。
     *      2. 根据 UseTask （节点1） 获取 OutgoingFlow 连线 以获取 到下一个节点 节点2（需要从 普通 转换到 会签）
     *      3. 根据 会签节点模型 ，添加需要的组件： MultiInstanceLoopCharacteristics  ParallelMultiInstanceBehavior。注意参数需要与 XML 配置中一致
     *      4. 完成节点1 任务，在完成任务时 传入需要会签的任务列表。此时 根据Bpmn Model 会创建 会签任务。
     *      5. 完成任务后，将 已添加组件 移除，将节点2 从 会签节点转换 成为 普通节点。
     *
     * @param taskId
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void changeNode(String taskId) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);

        // 节点 1
        UserTask currentNode = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());

        // 连线
        SequenceFlow sequenceFlow = currentNode.getOutgoingFlows().get(0);

        System.out.println("target:" + sequenceFlow.getTargetRef());

        // 测试节点 ,待转换节点
        UserTask nextNode = (UserTask) process.getFlowElement(sequenceFlow.getTargetRef());

        nextNode.setAssignee("${assignee}");

        // 多实例
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        multiInstanceLoopCharacteristics.setSequential(false);
        multiInstanceLoopCharacteristics.setInputDataItem("${assigneeList}");
        multiInstanceLoopCharacteristics.setElementVariable("assignee");


        // 注入循环控制
        nextNode.setLoopCharacteristics(multiInstanceLoopCharacteristics);

        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        // 创建任务实例
        UserTaskActivityBehavior userTaskActivityBehavior = processEngineConfiguration.getActivityBehaviorFactory().createUserTaskActivityBehavior(nextNode);
        // 创建behavior

        ParallelMultiInstanceBehavior behavior = new ParallelMultiInstanceBehavior(nextNode, userTaskActivityBehavior);
        nextNode.setBehavior(behavior);

        // 获取表达式解析工具
        behavior.setCollectionElementVariable("assignee");

        // 注入表达式
        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();

        System.out.println("expressionManager class :" + expressionManager.getClass().getName());

        behavior.setCollectionExpression(expressionManager.createExpression("${assigneeList}"));

        // 完成任务

        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("1");
        assigneeList.add("2");
        assigneeList.add("3");
        Map<String, Object> map = new HashMap<>();
        map.put("assigneeList", assigneeList);

        taskService.complete(taskId, map);

        // 变更回普通节点
        nextNode.setLoopCharacteristics(null);
        nextNode.setBehavior(null);


    }


}
