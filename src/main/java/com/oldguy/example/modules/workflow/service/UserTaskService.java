package com.oldguy.example.modules.workflow.service;

import com.oldguy.example.modules.common.exceptions.FormValidException;
import com.oldguy.example.modules.sys.services.UserEntityService;
import com.oldguy.example.modules.workflow.configs.WorkFlowConfiguration;
import com.oldguy.example.modules.workflow.dao.entities.HistoryTask;
import com.oldguy.example.modules.workflow.dao.entities.ProcessAuditStatus;
import com.oldguy.example.modules.workflow.dao.jpas.HistoryTaskMapper;
import com.oldguy.example.modules.workflow.dao.jpas.ProcessAuditStatusMapper;
import com.oldguy.example.modules.workflow.dto.TaskComment;
import com.oldguy.example.modules.workflow.dto.TaskEntityInfo;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * @author huangrenhao
 * @date 2019/1/18
 */
@Service
public class UserTaskService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private HistoryTaskMapper historyTaskMapper;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ProcessAuditStatusMapper processAuditStatusMapper;
    @Autowired
    private CommonWorkEntityService commonWorkEntityService;


    /**
     * 撤回上一节点
     * @param taskId
     */
    public void callBack(String taskId) {

        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (null == task) {
            throw new FormValidException("无效任务ID[ " + taskId + " ]");
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        if (null == processInstance) {
            throw new FormValidException("该流程已完成!无法回退");
        }

        // 获取流程定义对象
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);

        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();

        FlowNode sourceNode = (FlowNode) process.getFlowElement(task.getTaskDefinitionKey());
        taskList.forEach(obj -> {
            FlowNode currentNode = (FlowNode) process.getFlowElement(obj.getTaskDefinitionKey());
            // 获取原本流程连线
            List<SequenceFlow> outComingSequenceFlows = currentNode.getOutgoingFlows();

            // 配置反转流程连线
            SequenceFlow sequenceFlow = new SequenceFlow();
            sequenceFlow.setTargetFlowElement(sourceNode);
            sequenceFlow.setSourceFlowElement(currentNode);
            sequenceFlow.setId("callback-flow");

            List<SequenceFlow> newOutComingSequenceFlows = new ArrayList<>();
            newOutComingSequenceFlows.add(sequenceFlow);
            currentNode.setOutgoingFlows(newOutComingSequenceFlows);

            // 配置任务审批人
            Map<String, Object> variables = new HashMap<>(1);
            variables.put(WorkFlowConfiguration.DEFAULT_USER_TASK_ASSIGNEE, UserEntityService.getCurrentUserEntity().getUserId());
            // 完成任务
            taskService.complete(obj.getId(), variables);
            // 复原流程
            currentNode.setOutgoingFlows(outComingSequenceFlows);
        });

        // 更新流程状态
        updateAuditStatus(processInstance.getProcessInstanceId());
    }

    /**
     * 获取流程审核状态
     *
     * @param processInstanceId
     * @return
     */
    public int updateAuditStatus(String processInstanceId) {

        String auditCode = "";

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null != historicProcessInstance) {

            // 流程已完成
            if (null != historicProcessInstance.getEndTime()) {
                ProcessAuditStatus processAuditStatus = processAuditStatusMapper.findByProcessDefinitionIdAndUserTask(historicProcessInstance.getProcessDefinitionId(), WorkFlowConfiguration.PROCESS_END_EVENT_FLAG);
                if (null != processAuditStatus) {
                    auditCode = processAuditStatus.getAuditCode();
                }
            } else {
                // 流程未完成
                List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();
                if (!taskList.isEmpty()) {
                    HistoricTaskInstance historicTaskInstance = taskList.get(0);
                    ProcessAuditStatus processAuditStatus = processAuditStatusMapper.findByProcessDefinitionIdAndUserTask(historicTaskInstance.getProcessDefinitionId(), historicTaskInstance.getTaskDefinitionKey());
                    if (null != processAuditStatus) {
                        auditCode = processAuditStatus.getAuditCode();
                    }
                }
            }
        }

        // 更新业务
        return commonWorkEntityService.updateAuditStatus(historicProcessInstance.getBusinessKey(), auditCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public void complete(String taskId, String comment, String flowFlag) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (null == task) {
            throw new RuntimeException("不存在任务 [ " + taskId + " ] ");
        }

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

        // 完成任务
        Map<String, Object> variables = new HashMap<>(2);
        variables.put(WorkFlowConfiguration.WORKFLOW_FLOW_FLAG, flowFlag);

        // 添加批注
        if (!org.springframework.util.StringUtils.isEmpty(comment)) {
            Authentication.setAuthenticatedUserId(UserEntityService.getCurrentUserEntity().getUserId());
            taskService.addComment(taskId, pi.getProcessInstanceId(), comment);
        }

        HistoryTask historyTask = new HistoryTask();
        historyTask.setTaskId(taskId);
        historyTask.setTaskName(task.getName());
        historyTask.setStatus(1);
        historyTask.setCreateTime(new Date());
        historyTask.setCreatorId(UserEntityService.getCurrentUserEntity().getUserId());
        historyTask.setCreatorName(UserEntityService.getCurrentUserEntity().getUsername());
        historyTask.setComment(comment);
        historyTask.setFlowFlag(flowFlag);
        historyTask.setBusinessKey(pi.getBusinessKey());
        historyTask.setProcessDefineId(pi.getProcessDefinitionId());
        historyTask.setProcessDefineKey(pi.getProcessDefinitionKey());
        historyTask.setProcessInstanceId(pi.getProcessInstanceId());
        historyTask.setBusinessKey(pi.getBusinessKey());

        historyTaskMapper.save(historyTask);
        taskService.complete(taskId, variables);
    }

    /**
     * 获取当前任务流程图
     *
     * @param taskId
     * @return
     */
    public InputStream currentProcessInstanceImage(String taskId) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // ID 为 流程定义Key
        Process process = bpmnModel.getProcessById(processDefinition.getKey());

        FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        List<String> highLightedActivities = new ArrayList<>();
        highLightedActivities.add(flowElement.getId());

        InputStream inputStream = generator.generateDiagram(bpmnModel, "jpg", highLightedActivities, Collections.emptyList(), "宋体", "宋体", "宋体", null, 2.0);
        return inputStream;
    }

    /**
     * 完成任务
     *
     * @param pi
     * @param params
     * @param businessKey
     */
    public void completeTaskByProcessInstance(ProcessInstance pi, Map<String, Object> params, String businessKey) {

        params = params == null ? new HashMap<>(16) : params;
        params.put(WorkFlowConfiguration.DEFAULT_USER_TASK_ASSIGNEE, UserEntityService.getCurrentUserEntity().getUserId());

        List<Task> taskList = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        if (!taskList.isEmpty()) {
            Task task = taskList.get(0);

            // 自定义历史任务
            HistoryTask historyTask = new HistoryTask();
            historyTask.setTaskId(task.getId());
            historyTask.setTaskName(task.getName());
            historyTask.setStatus(1);
            historyTask.setCreateTime(new Date());
            historyTask.setCreatorId(UserEntityService.getCurrentUserEntity().getUserId());
            historyTask.setCreatorName(UserEntityService.getCurrentUserEntity().getUsername());
            historyTask.setProcessDefineId(pi.getProcessDefinitionId());
            historyTask.setProcessDefineKey(pi.getProcessDefinitionKey());
            historyTask.setProcessInstanceId(pi.getProcessInstanceId());
            historyTask.setBusinessKey(businessKey);
            historyTaskMapper.save(historyTask);

            // 完成默认任务
            taskService.complete(task.getId(), params);
        }
    }

    /**
     * 获取系统批注信息
     *
     * @param processInstanceId
     * @return
     */
    public List<TaskComment> getComments(String processInstanceId) {
        List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);
        List<TaskComment> list = new ArrayList<>(comments.size());
        comments.forEach(obj -> {
            TaskComment comment = new TaskComment();
            comment.setId(obj.getId());
            comment.setTaskId(obj.getTaskId());
            comment.setMessage(obj.getFullMessage());
            comment.setProcessInstanceId(obj.getProcessInstanceId());
            comment.setTime(obj.getTime());
            comment.setUserId(obj.getUserId());
            list.add(comment);
        });
        return list;
    }

    /**
     * 历史任务详情
     *
     * @param taskId
     * @return
     */
    public TaskEntityInfo historyTaskInfo(String taskId) {

        TaskEntityInfo info = new TaskEntityInfo();


        return info;
    }

    /**
     * 当前任务详情
     *
     * @param taskId
     * @return
     */
    public TaskEntityInfo currentTaskInfo(String taskId) {

        TaskEntityInfo info = new TaskEntityInfo();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (null == task) {
            throw new FormValidException("未找到当前任务:[ " + taskId + " ]");
        }

        // 获取用户ID信息
        Set<String> userIdSet = trainTaskToTaskInfo(task, info);

        // 设置任务详情
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        info.setProcessDefinitionName(processInstance.getProcessDefinitionName());
        info.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        info.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        info.setBusinessKey(processInstance.getBusinessKey());
        info.setEntityId(Long.valueOf(WorkFlowConfiguration.trainFormBusinessKey(processInstance.getBusinessKey()).get(WorkFlowConfiguration.PROCESS_INSTANCE_ID)));

        Map<String, String> usernameMap = userEntityService.getUsernameMapByUserIds(userIdSet);
        info.setAssignerNames(new HashSet<>(usernameMap.values()));

        return info;
    }

    /**
     * 获取当前用户任务列表
     *
     * @param userId
     * @return
     */
    public List<TaskEntityInfo> currentTaskList(String userId) {

        List<TaskEntityInfo> records = new ArrayList<>();
        List<Task> taskList = taskService.createTaskQuery().taskCandidateOrAssigned(userId).active().orderByTaskCreateTime().desc().list();

        if (taskList.isEmpty()) {
            return records;
        }

        Map<String, TaskEntityInfo> taskMap = new HashMap<>(16);
        Map<String, Set<String>> userIdsMap = new HashMap<>(16);

        Set<String> allUserIdSet = new HashSet<>();

        // 设置任务信息
        taskList.forEach(task -> {

            TaskEntityInfo entity = new TaskEntityInfo();
            records.add(entity);
            taskMap.put(task.getProcessInstanceId(), entity);

            Set<String> userIdSet = trainTaskToTaskInfo(task, entity);

            userIdsMap.put(task.getProcessInstanceId(), userIdSet);
            allUserIdSet.addAll(userIdSet);

        });

        // 注入流程详情信息
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceIds(taskMap.keySet()).list();
        processInstanceList.forEach(processInstance -> {
            TaskEntityInfo info = taskMap.get(processInstance.getProcessInstanceId());
            info.setBusinessKey(processInstance.getBusinessKey());
            info.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
            info.setProcessDefinitionName(processInstance.getProcessDefinitionName());
            info.setProcessDefinitionId(processInstance.getProcessDefinitionId());
            info.setEntityId(Long.valueOf(WorkFlowConfiguration.trainFormBusinessKey(processInstance.getBusinessKey()).get(WorkFlowConfiguration.PROCESS_INSTANCE_ID)));

        });

        Map<String, String> usernameMap = userEntityService.getUsernameMapByUserIds(allUserIdSet);

        // 注入用户名
        userIdsMap.forEach((processInstanceId, idSet) -> {
            Set<String> usernameSet = new HashSet<>();
            idSet.forEach(key -> {
                usernameSet.add(usernameMap.get(key));
            });

            TaskEntityInfo info = taskMap.get(processInstanceId);
            info.setAssignerNames(usernameSet);
        });

        // 时间排序
        Collections.sort(records);

        return records;
    }

    /**
     * 任务转换为任务详情
     *
     * @param task
     * @param entity
     * @return
     */
    private Set<String> trainTaskToTaskInfo(Task task, TaskEntityInfo entity) {
        entity.setProcessInstanceId(task.getProcessInstanceId());
        entity.setTaskDefinitionKey(task.getTaskDefinitionKey());
        entity.setTaskName(task.getName());
        entity.setId(task.getId());
        entity.setCreateTime(task.getCreateTime());

        // 设置时间
        long durationTimeStamp = System.currentTimeMillis() - entity.getCreateTime().getTime();
        durationTimeStamp = durationTimeStamp / (24 * 3600 * 1000);
        entity.setDuration(durationTimeStamp);

        // 设置任务执行人
        Set<String> userIdSet = new HashSet<>();
        if (StringUtils.isNotBlank(task.getAssignee())) {
            userIdSet.add(task.getAssignee());
        } else {
            List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(task.getId());
            identityLinkList.forEach(obj -> {
                userIdSet.add(obj.getUserId());
            });
        }
        entity.setAssigners(userIdSet);
        return userIdSet;
    }

}
