package com.oldguy.example.modules.workflow.service.activiti.impls;

import com.oldguy.example.modules.common.utils.Log4jUtils;
import com.oldguy.example.modules.workflow.dao.entities.TransformEntityInfo;
import com.oldguy.example.modules.workflow.dao.jpas.TransformEntityInfoMapper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * @ClassName: MultiWorkFLowService
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/23 0023 下午 4:55
 **/
@Service
public class MultiWorkFLowService extends AbstractMultiWorkFLowService {

    @Autowired
    private TransformEntityInfoMapper transformEntityInfoMapper;


    /**
     * 完成任务
     *
     * @param taskId
     * @param data
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void completeTask(String taskId, Map<String, Object> data) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        /**
         *  获取流程实例是否被转换
         */
        TransformEntityInfo transformEntityInfo = transformEntityInfoMapper.findByProcessInstance(task.getProcessInstanceId());

        /**
         *  转换进行特殊处理
         */
        if (null != transformEntityInfo && task.getTaskDefinitionKey().equals(transformEntityInfo.getTaskDefineKey())) {

            BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
            Process process = bpmnModel.getProcesses().get(0);
            /**
             *  获取当前流程节点，将当前节点 从 普通节点 - > 会签节点
             */
            UserTask currentNode = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());

            currentNode.setBehavior(createMultiInstanceBehavior(currentNode, transformEntityInfo.getSequential()));
            currentNode.setLoopCharacteristics(createMultiInstanceLoopCharacteristics(transformEntityInfo.getSequential()));

            /**
             *  完成任务
             */
            taskService.complete(taskId, data);

            /**
             *  将节点 从 会签节点 - > 普通节点
             */
            currentNode.setBehavior(null);
            currentNode.setLoopCharacteristics(null);
            return;
        }

        /**
         *  默认完成任务
         */
        taskService.complete(taskId, data);
    }

    @Override
    public void covertToMultiInstance(String taskId, boolean sequential, Map<String, Object> data) {
        covertToMultiInstance(taskId, sequential, ASSIGNEE_USER_EXP, data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void covertToMultiInstance(String taskId, boolean sequential, String assigneeExp, Map<String, Object> data) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);

        // 节点 1
        UserTask currentNode = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());

        // 连线
        SequenceFlow sequenceFlow = currentNode.getOutgoingFlows().get(0);

        // 测试节点 ,待转换节点
        UserTask nextNode = (UserTask) process.getFlowElement(sequenceFlow.getTargetRef());

        nextNode.setAssignee(assigneeExp);


        /**
         *  设置解释器，普通节点将会变成会签节点
         */
        nextNode.setLoopCharacteristics(createMultiInstanceLoopCharacteristics(sequential));
        nextNode.setBehavior(createMultiInstanceBehavior(nextNode, sequential));

        /**
         *  完成前置任务
         */
        taskService.complete(taskId, data);

        /**
         *  将节点变回普通节点
         */
        nextNode.setLoopCharacteristics(null);
        nextNode.setBehavior(null);


        /**
         * 持久化 节点变更信息
         */
        TransformEntityInfo entity = new TransformEntityInfo();

        entity.setTaskDefineKey(nextNode.getId());
        entity.setProcessInstanceId(task.getProcessInstanceId());
        entity.setCreateTime(new Date());
        entity.setStatus(1);
        entity.setSequential(true);
        transformEntityInfoMapper.save(entity);

    }
}
