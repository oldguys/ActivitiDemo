##Activiti 6.0 动态扭转流程节点(节点A -> 节点N(B,C,D))

> **业务场景**： 在流程开发中，经常出现那种特殊情况，如流程需要强行转到另外的节点（一个/多个）。这个时候，可以通过修改 Bpmn Model 的 SequenceFlow 来进行修改
> **环境**：springboot + activiti6.0
>

业务描述： 当任务执行到 测试节点A 可以 动态指定到 任意节点（B，C，D，E）（一个任务或多个任务）。
![流程图](https://upload-images.jianshu.io/upload_images/14387783-3657a7f0fd176563.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

实现思路：
1. 通过流程缓存的 Bpmn Model ，获取到 **当前任务节点** 和 **需要调整任务节点**。
2. 根据 节点 创建流程连线集合 List<SequenceFlow>  (如：节点A -> 节点N（B，C，D，E）)。并替换原本的OutgoingFlows
3. 完成任务，注意：在定义流程阶段，得把流程标识清楚，如：
```
节点B：assignee = ${assignee_b}
节点C：assignee = ${assignee_c}
```
以防止设置审批人冲突。
4.完成任务后，注意把 OutgoingFlows 替换为原本的。（经过测试，就是不替换回来也不会持久化到数据库，导致流程异常。听说基于ThreadLocal，所以不会导致冲突。）

**注意**
业务问题：当生成多个任务的时候，注意完成任务需要特殊完成，否则流程会出现多重审批。
 

方法：
```

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

            /**
             *  当前流程节点
             */
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

    }

```

测试例子：

1. 希望流程转派到4个节点，同时生成4个任务。

![转到4个节点](https://upload-images.jianshu.io/upload_images/14387783-5ad3f925d6f40363.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2. 开启流程实例：processInstanceId = “97517”；当前任务节点A taskId = “97522”

![3816be3d357605c609bd81907395839.png](https://upload-images.jianshu.io/upload_images/14387783-70ea1b6e4955bcf1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

3. 完成任务，生产4个测试任务

![973fec9dd9198d138c8b282eac44c2e.png](https://upload-images.jianshu.io/upload_images/14387783-fc2c2b41c6c965ad.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

