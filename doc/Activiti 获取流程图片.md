#Activiti 获取流程图片

> 基于 Activiti 6.0
>  获取流程定义图片：1.流程定义；2.任务实例

###### 1.  获取流程定义图
Controller 方法
```
    @GetMapping("processDefinitionImage")
    public void processDefinitionImage(String processDefinitionId, HttpServletResponse response) throws IOException {

        if (StringUtils.isBlank(processDefinitionId)) {
            throw new FormValidException("processDefinitionId 不能为空!");
        }
        InputStream inputStream = processService.getDefinitionImage(processDefinitionId);
        OutputStream outputStream = response.getOutputStream();
        HttpUtils.copyImageStream(inputStream, outputStream);
    }
```
方法:processService.getDefinitionImage
```
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

```
使用 apache common-io包进行流操作
```
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-io</artifactId>
	<version>1.3.2</version>
</dependency>
```
通用流处理类
```
package com.oldguy.example.modules.workflow.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author huangrenhao
 * @date 2019/1/22
 */
public class HttpUtils {

    public static void copyImageStream(InputStream inputStream,OutputStream outputStream){
        try {
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

```
流程定义图
![processDefinitionImage.png](https://upload-images.jianshu.io/upload_images/14387783-07eac05d305f8be6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



###### 1.  获取任务图
>取任务图的时候，需要涉及动态节点，而流程定义图是静态的，所以需要涉及动态流程图创建。其中Activiti 提供了流程图创建类。
> 1. Activiti 是基于BPMN流程图的，所以在Activiti中会有一个类: **org.activiti.bpmn.model.BpmnModel** ,l来描述 BPMN.xml 的信息。
> 2. 由BpmnModel可以获取流程定义类: **org.activiti.bpmn.model.Process**，用于描述流程各个节点与信息
> 3. 通过当前任务节点的Key，来获取流程信息流程节点类: **org.activiti.bpmn.model.FlowElement**，到此就可以获取到流程信息
> 4. 通过Activiti 提供到图片渲染类：**org.activiti.image.impl.DefaultProcessDiagramGenerator**，根据BpmnModel对象，动态生成流程图。

Controller 方法
```
    @GetMapping("ProcessInstanceImage/{taskId}")
    public void currentProcessInstanceImage(@PathVariable("taskId") String taskId, HttpServletResponse response) throws IOException {
        InputStream inputStream = userTaskService.currentProcessInstanceImage(taskId);
        OutputStream outputStream = response.getOutputStream();
        HttpUtils.copyImageStream(inputStream, outputStream);
    }
```
userTaskService.currentProcessInstanceImage(taskId); 获取当前节点流程任务图
```
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

//        UserTask userTask = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());
        // 流程节点ID
        FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());

        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();


        List<String> highLightedActivities = new ArrayList<>();
        highLightedActivities.add(flowElement.getId());

//     生成流程图
//        InputStream inputStream = generator.generateJpgDiagram(bpmnModel);
//        InputStream inputStream = generator.generatePngDiagram(bpmnModel);
//        InputStream inputStream = generator.generateDiagram(bpmnModel, "jpg", highLightedActivities);

// 生成图片
        InputStream inputStream = generator.generateDiagram(bpmnModel, "jpg", highLightedActivities, Collections.emptyList(), "宋体", "宋体", "宋体", null, 2.0);
        return inputStream;
    }
```

当前任务流程图
![taskDefinitionImage.jpg](https://upload-images.jianshu.io/upload_images/14387783-82b4a794f9019f81.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

到此完成了流程图获取。