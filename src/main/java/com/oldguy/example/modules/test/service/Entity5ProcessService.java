package com.oldguy.example.modules.test.service;

import com.oldguy.example.modules.common.services.BaseService;
import com.oldguy.example.modules.sys.services.UserEntityService;
import com.oldguy.example.modules.test.dao.entities.Entity4Process;
import com.oldguy.example.modules.test.dao.entities.Entity5Process;
import com.oldguy.example.modules.test.dao.jpas.Entity3ProcessMapper;
import com.oldguy.example.modules.test.dao.jpas.Entity5ProcessMapper;
import com.oldguy.example.modules.workflow.service.ProcessService;
import com.oldguy.example.modules.workflow.service.UserTaskService;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ClassName: Entity5ProcessService
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/6 0006 上午 10:57
 **/
@Service
public class Entity5ProcessService extends BaseService {


    @Autowired
    private Entity5ProcessMapper entity5ProcessMapper;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;

    @Transactional(rollbackFor = Exception.class)
    public String openProcessInstance() {

        Entity5Process entity5Process = new Entity5Process();
        newInstance(entity5Process);
        entity5ProcessMapper.save(entity5Process);

        String key = Entity5Process.class.getSimpleName();

        Map<String, Object> data = new HashMap<>(1);
        data.put("assignee", "1");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, key + entity5Process.getId(), data);

        System.out.println(processInstance.getId());


        return "";
    }

    /**
     *  动态流转节点 （1 ： N）
     *  非直线流程（任务开始时间） A > B = C > D ，在A任务完成时，同时开启 B ，C 任务。
     *
     *  解决思路 ： 改动当前任务的流转线（新增），将流程节点流向改为多个节点。这样就可以
     *  动态扭转流程走向。
     *
     *  注意：
     *      1. 改变 Bpmn Model.process 之后，再 完成任务。
     *      2. 一开始设置 .bpmn 文件的时候 在注入审批人 标志必须 可区分 如: assignee = ${assignee_b};assignee = ${assignee_c}
     *      以防止 出现审批人 出错。
     *      3. 最好在 完成任务之后，将流程流向更改回默认。（虽然已经确认 process 基于ThreadLocal ，但是防止意外）
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void test(String taskId, String... useTasks) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);

        /**
         *  获取需要 指向的流程节点，备用
         */
        List<FlowElement> targetUserTaskList = new ArrayList<>(useTasks.length);

        for (int i = 0; i < useTasks.length; i++) {

            String taskName = useTasks[i];
            System.out.println(taskName);

            targetUserTaskList.add(process.getFlowElement(taskName));
        }

        System.out.println("------------------------------");
        System.out.println();

        /**
         *  获取当前任务节点
         */
        FlowElement currentElement = process.getFlowElement(task.getTaskDefinitionKey());

        /**
         *  生成 需要指向的节点连线。
         */
        List<SequenceFlow> tempSequenceFlows = new ArrayList<>(targetUserTaskList.size());

        targetUserTaskList.forEach(obj -> {
            SequenceFlow flow = new SequenceFlow();

            flow.setSourceFlowElement(currentElement);
            flow.setSourceRef(currentElement.getId());
            flow.setTargetFlowElement(obj);
            flow.setTargetRef(obj.getId());

            tempSequenceFlows.add(flow);
        });


        /**
         *  修改流程指向，并完成任务
         */
        if (currentElement instanceof UserTask) {

            UserTask current = (UserTask) currentElement;
            // 缓存原本的流向
            List<SequenceFlow> sourceFlows = current.getOutgoingFlows();
            // 使用临时流向
            current.setOutgoingFlows(tempSequenceFlows);

            Map<String, Object> data = new HashMap<>(1);

            data.put("assignee", "2");
            taskService.complete(taskId, data);

            /**
             * 完成任务后，替换为原本的流向
             *
             *  （虽然经过测试，就算不改变回来，也不会持久到数据库，下次调用不会改变）
             */
            current.setOutgoingFlows(sourceFlows);
        } else {
            System.out.println("不是任务节点.................");
        }

        System.out.println("------------------------------");


    }


}
