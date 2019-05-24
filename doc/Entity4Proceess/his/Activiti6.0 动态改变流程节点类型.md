##Activiti6.0 动态改变流程节点类型（普通-> 会签）

> **业务场景**：在流程开发中，需要临时变更节点，将节点从 普通节点 -> 会签节点
> **环境**：springboot +activiti6.0
>  GitHub  [https://github.com/oldguys/ActivitiDemo](https://github.com/oldguys/ActivitiDemo)
>

**业务需求**：将 测试节点 转换为 会签节点
![测试节点](https://upload-images.jianshu.io/upload_images/14387783-c831b4b207064a63.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

实现思路
```

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
     */

```

**实现方法**：
```
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
```

**测试例子**

1. 开启流程 流程实例：102502，节点1:102505

![1.jpg](https://upload-images.jianshu.io/upload_images/14387783-1962d552ea4e9abf.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2. 完成任务

![3.jpg](https://upload-images.jianshu.io/upload_images/14387783-7bb076d639275dce.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

3. 将 **普通节点** 转成 **会签节点**

![2.jpg](https://upload-images.jianshu.io/upload_images/14387783-9e085f766aefddd4.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
