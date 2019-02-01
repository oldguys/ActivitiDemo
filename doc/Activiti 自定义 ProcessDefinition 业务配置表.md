## Activiti 自定义 ProcessDefinition 业务配置表


> 流程审批基本都是通用的，但是业务审批却是多态的，一模一样流程图的两个不同流程，调用的方法截然不同，而获取资源的写法基本一致，为了降低代码耦合度，可以定制通过的审批信息获取表，来获取数据。
>
> 基于Activiti 6.0
> GitHub  [https://github.com/oldguys/ActivitiDemo](https://github.com/oldguys/ActivitiDemo)
>

#####设计思路:
1.通过流程图获取各个流程节点，对流程节点进行分类。
2.然后根据任务 **taskDefinitionKey** 与 **ProcessDefinitionId** 将自定义表与流程串起来。
3. 连线配置同理

功能界面图:
![节点状态.png](https://upload-images.jianshu.io/upload_images/14387783-5b85f11fd3952f98.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![连线配置.png](https://upload-images.jianshu.io/upload_images/14387783-e3e65528f5396c59.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 1.审批状态实体
```
/**
 * @author ren
 * @date 2019/1/23
 */
@Entity
@Data
public class ProcessAuditStatus extends BaseEntity {

    /**
     *  流程定义Key
     */
    private String processDefinitionKey;

    /**
     *  流程定义ID
     */
    private String processDefinitionId;

    /**
     *  节点
     */
    private String userTask;

    /**
     *  审核状态码
     */
    private String auditCode;

    /**
     *  审核状态描述
     */
    private String auditMessage;

}
```
###### 2. 连线配置实体
```
/**
 * @Date: 2019/1/26 0026
 * @Author: ren
 * @Description:
 */
@Entity
@Data
public class ProcessTaskConfig extends BaseEntity{

    /**
     *  流程定义ID
     */
    private String processDefinitionId;

    /**
     *  流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 流程判定标识
     */
    private String flowFlag;

    /**
     *  连线ID
     */
    private String flowId;

    /**
     *  显示按钮名称
     */
    private String btn;

    /**
     *  相应后台链接
     */
    private String url;
}
```
获取流程图节点信息:
com.oldguy.example.modules.workflow.service.ProcessService
```
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

        // 获取当前已存在数据库中 节点状态记录
        info.setElementValueMap(processAuditStatusService.getMap(info.getKey(), processDefinitionId));
        // 获取当前已存在数据库中 连线配置记录
        info.setTaskConfigMap(processTaskConfigService.getProcessTaskConfigMap(info.getKey(), processDefinitionId));

        return info;
    }

```

以上是连线配置 设置界面的数据相关信息。
下面是怎样关联 **流程模型** 与 **审批记录和连线配置**

##### 获取连线按钮
1. 获取当前任务连线（通过获取当前任务的ID，从Bpmn实例中获取输出连线）
com.oldguy.example.modules.workflow.service.ProcessService
```
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
```

2. 配置连线信息
com.oldguy.example.modules.workflow.service.AbstractProcessInstanceService
```
    @Override
    public List<WorkBtn> getWorkBtnList(String processDefinitionId, String taskDefinitionKey, List<SequenceFlow> outPutLinks) {

        List<WorkBtn> list = new ArrayList<>(outPutLinks.size());
        Map<String, ProcessTaskConfig> processTaskConfigMap = processTaskConfigService.getProcessTaskConfigMap(className, processDefinitionId);

        outPutLinks.forEach(obj -> {
            // 配置连线信息
            ProcessTaskConfig processTaskConfig = processTaskConfigMap.get(obj.getId());
            if(null != processTaskConfig){
                list.add(new WorkBtn(processTaskConfig.getBtn(), processTaskConfig.getUrl(), processTaskConfig.getFlowFlag()));
            }
        });

        return list;
    }


```
这样就完成，通过连线获取动态流程按钮。

com.oldguy.example.modules.workflow.service.UserTaskService 获取流程审核状态
```
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
            }else{
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
        return commonWorkEntityService.updateAuditStatus(historicProcessInstance.getBusinessKey(),auditCode);
    }

```

获取当前流程状态
```
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
```