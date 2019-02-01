#Activiti 流程回退到上一节点

> 在流程开发时，时常会遇到一个误操作的场景。正常审批：节点1 ->节点2。节点1审核人操作提交任务后，发现原本填写信息有问题，希望修改，但是流程已经到了审批节点2。
>
> 基于Activiti 6.0
> GitHub  [https://github.com/oldguys/ActivitiDemo](https://github.com/oldguys/ActivitiDemo)

功能图:

![撤回前.jpg](https://upload-images.jianshu.io/upload_images/14387783-d5e58cd88a0d50d0.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![撤回操作.jpg](https://upload-images.jianshu.io/upload_images/14387783-cde8504051ef7d49.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![撤回后.jpg](https://upload-images.jianshu.io/upload_images/14387783-68bd9176a65606f3.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

解决思路:
1.获取当前任务节点，将节点连线反转，走回上一节点，再对流程进行修改回来。

```

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
```

获取历史任务SQL
其中 history_task  为自定义历史任务类。
```
        SELECT
        a.task_id,
        a.task_name,
        a.business_key,
        a.process_instance_id,
        a.process_define_id,
        a.process_define_key,
        a.`comment`,
        a.flow_flag,
        a.creator_id,
        a.creator_name,
        a.create_time,
        a.id,
        a.`status`
        , NAME_ processDefineName
        , g.ID_ lastCommit
        FROM
        history_task a
        LEFT JOIN act_re_procdef b ON a.process_define_id = b.ID_
-- 获取 最后一次操作标示
        LEFT JOIN (
            SELECT
              ID_
            FROM
              act_hi_taskinst e
            INNER JOIN (
              SELECT
                MAX(END_TIME_) END_TIME_,
              PROC_INST_ID_
              FROM
              act_hi_taskinst d
              GROUP BY
              PROC_INST_ID_
            ) f ON e.END_TIME_ = f.END_TIME_ AND e.PROC_INST_ID_ = f.PROC_INST_ID_
            WHERE
            e.PROC_INST_ID_ IN (
              SELECT
                PROC_INST_ID_
              FROM
                act_hi_procinst c
              WHERE
                PROC_INST_ID_ IN (
                  SELECT DISTINCT
                    a.PROC_INST_ID_
                  FROM
                    act_hi_taskinst a
                  LEFT JOIN act_hi_identitylink b ON a.ID_ = b.TASK_ID_
  --              WHERE
  --                USER_ID_ = #{userId} OR a.ASSIGNEE_ = #{userId}
                )
              AND ISNULL(END_TIME_)
            )
        ) g ON a.task_id = g.ID_
--        WHERE
--        creator_id = #{userId}
        ORDER BY a.id DESC
```
