package com.oldguy.example.modules.workflow.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.oldguy.example.modules.common.exceptions.FormValidException;
import com.oldguy.example.modules.sys.services.UserEntityService;
import com.oldguy.example.modules.workflow.configs.WorkFlowConfiguration;
import com.oldguy.example.modules.workflow.dao.entities.UserProcessInstance;
import com.oldguy.example.modules.workflow.dao.jpas.UserProcessInstanceMapper;
import com.oldguy.example.modules.workflow.dto.ProcessDefinitionInfo;
import com.oldguy.example.modules.workflow.dto.TaskFlow;
import com.oldguy.example.modules.workflow.dto.UserProcessInstanceQueryForm;
import com.oldguy.example.modules.workflow.service.entities.ProcessAuditStatusService;
import com.oldguy.example.modules.workflow.service.entities.ProcessTaskConfigService;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

/**
 * @author ren
 * @date 2019/1/18
 * @Description 流程详情
 */
@Service
public class ProcessService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private UserProcessInstanceMapper userProcessInstanceMapper;
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private ProcessAuditStatusService processAuditStatusService;
    @Autowired
    private ProcessTaskConfigService processTaskConfigService;

    /**
     *  激活流程
     * @param processInstanceId
     */
    public void activateProcessInstance(String processInstanceId){
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null == processInstance) {
            throw new FormValidException("不存在正在执行的流程实例:[ " + processInstanceId + " ]");
        }
        runtimeService.activateProcessInstanceById(processInstanceId);
    }

    /**
     *  流程挂起
     * @param processInstanceId
     */
    public void suspendProcessInstance(String processInstanceId) {

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null == processInstance) {
            throw new FormValidException("不存在正在执行的流程实例:[ " + processInstanceId + " ]");
        }
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 流程定义详情配置页
     *
     * @param processDefinitionId
     * @return
     */
    public ProcessDefinitionInfo processDefinitionInfo(String processDefinitionId) {

        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();

        ProcessDefinitionInfo info = new ProcessDefinitionInfo(processDefinition.getId(), processDefinition.getKey(), processDefinition.getName(), processDefinition.getDeploymentId(), processDefinition.getVersion());

        // 任务节点
        List<ProcessDefinitionInfo.TaskElement> elements = new ArrayList<>();
        info.setElements(elements);

        // 任务连线
        List<TaskFlow> taskFlowList = new ArrayList<>();
        info.setTaskFlowList(taskFlowList);

        for (FlowElement element : flowElements) {
            if (element instanceof UserTask) {
                elements.add(new ProcessDefinitionInfo.TaskElement(element.getId(), element.getName()));
            } else if (element instanceof EndEvent) {
                elements.add(new ProcessDefinitionInfo.TaskElement(element.getId(), "完成"));
            }

            if (element instanceof SequenceFlow) {
                SequenceFlow flow = (SequenceFlow) element;

                // 获取源节点名称
                FlowElement sourceTask = process.getFlowElement(flow.getSourceRef());
                String sourceTaskName = getTaskNameByFlowElement(process, sourceTask, "source");

                // 获取指向节点名称
                FlowElement targetTask = process.getFlowElement(flow.getTargetRef());
                String targetTaskName = getTaskNameByFlowElement(process, targetTask, "target");


                taskFlowList.add(new TaskFlow(flow.getId(), flow.getName(), flow.getConditionExpression(), sourceTaskName, targetTaskName));
            }
        }

        // 配置节点值
        info.setElementValueMap(processAuditStatusService.getMap(info.getKey(), processDefinitionId));
        info.setTaskConfigMap(processTaskConfigService.getProcessTaskConfigMap(info.getKey(), processDefinitionId));

        return info;
    }

    /**
     * 获取节点名称
     *
     * @param process
     * @param flowElement
     * @param type
     * @return
     */
    private String getTaskNameByFlowElement(Process process, FlowElement flowElement, String type) {

        if (null != flowElement && !(flowElement instanceof Gateway)) {
            return flowElement.getName();
        } else if (null != flowElement && flowElement instanceof Gateway) {

            Gateway gateway = (Gateway) flowElement;
            if (type.equals("source")) {
                if (gateway.getIncomingFlows().size() == 1) {
                    flowElement = process.getFlowElement(gateway.getIncomingFlows().get(0).getSourceRef());
                }
            } else {
                if (gateway.getOutgoingFlows().size() == 1) {
                    flowElement = process.getFlowElement(gateway.getOutgoingFlows().get(0).getSourceRef());
                }
            }
            if (null != flowElement) {
                return flowElement.getName();
            }
        }

        return "";
    }


    /**
     * 获取流程信息
     *
     * @param page
     * @param form
     * @return
     */
    public Page<UserProcessInstance> findByPage(Page<UserProcessInstance> page, UserProcessInstanceQueryForm form) {

        List<UserProcessInstance> userProcessInstances = userProcessInstanceMapper.findByPage(page, form);
        page.setRecords(userProcessInstances);

        return page;
    }

    /**
     * 获取流程定义图片
     *
     * @param processDefinitionId
     * @return
     */
    public InputStream getDefinitionImage(String processDefinitionId) {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());
        return inputStream;
    }

    /**
     * 开启流程 + 启动第一步
     *
     * @param processKey
     * @param id
     * @param userId
     * @return
     */
    public ProcessInstance openProcessInstanceWithFirstCommit(String processKey, Long id, String userId) {

        ProcessInstance processInstance = openProcessInstance(processKey, id, userId);
        // 完成任务
        userTaskService.completeTaskByProcessInstance(processInstance, null, processKey + "." + id);

        return processInstance;
    }

    /**
     * 开启流程实例
     *
     * @param processKey 流程Key
     * @param id         流程ID
     * @param userId     流程执行人
     * @return
     */
    public ProcessInstance openProcessInstance(String processKey, Long id, String userId) {

        Map<String, Object> params = new HashMap(2);
        params.put(WorkFlowConfiguration.DEFAULT_USER_TASK_ASSIGNEE, userId);

        // 配置流程开启人
        identityService.setAuthenticatedUserId(userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, params);
        runtimeService.updateBusinessKey(processInstance.getId(), processKey + "." + id);

        return processInstance;
    }

    /**
     * 获取输出路线
     *
     * @param processInstanceId
     * @param taskDefinitionKey
     * @return
     */
    public List<SequenceFlow> getOutputLinkList(String processInstanceId, String taskDefinitionKey) {

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        // ID 为 流程定义Key
        Process process = bpmnModel.getProcessById(pi.getProcessDefinitionKey());
        UserTask userTask = (UserTask) process.getFlowElement(taskDefinitionKey);

        List<SequenceFlow> outGoingFlows = userTask.getOutgoingFlows();

        // 判断是否排他网关，是则获取下一节点
        if (outGoingFlows.size() == 1) {
            SequenceFlow sequenceFlow = outGoingFlows.get(0);
            FlowElement nextElement = sequenceFlow.getTargetFlowElement();
            if (nextElement instanceof ExclusiveGateway) {
                outGoingFlows = ((ExclusiveGateway) nextElement).getOutgoingFlows();
            }
        }

        return outGoingFlows;
    }


    public List<ProcessDefinitionInfo> getProcessDefinitions() {

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
        List<ProcessDefinitionInfo> records = new ArrayList<>(processDefinitions.size());

        processDefinitions.forEach(obj -> {
            records.add(new ProcessDefinitionInfo(obj.getId(), obj.getKey(), obj.getName(), obj.getDeploymentId(), obj.getVersion()));
        });

        return records;
    }


}
