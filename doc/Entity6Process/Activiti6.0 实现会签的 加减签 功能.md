###Activiti6.0 实现会签的 加减签 功能

> 业务描述： 进行会签任务时候，时常会遇到任务执行中，需要进行加签，减签。本章主要描述如何实现
> 版本：SpringBoot 1.5.19.RELEASE + Activiti 6.0
>  GitHub  [https://github.com/oldguys/ActivitiDemo](https://github.com/oldguys/ActivitiDemo)


**前置知识：**
> 1.  org.activiti.engine.impl.interceptor.Command：任务命令接口，Activiti具有任务执行机制，都是基于这个接口进行实现。如：
> org.activiti.engine.impl.cmd.CompleteTaskCmd：任务完成命令
> 2. org.activiti.engine.ManagementService: 任务管理服务接口，负责管理服务,用于完成 Command 任务
> 3. org.activiti.engine.impl.persistence.entity.ExecutionEntityManager：用于管理Execution。
> 4. Context.getAgenda().planContinueMultiInstanceOperation(newExecution); : 任务时间轴
> 5. 多实例任务行为解释器 
> org.activiti.bpmn.model.MultiInstanceLoopCharacteristics： 用于生成多实例任务
org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior：并行任务解释器;
> org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior: 串行任务解释器
> 6. Activiti 是根据execution进行 流转的，在会签任务的时候，会在生成 子级（多个）execution，当所有子任务都完成的时候，销毁 子 execution，回到 父级 execution 进行流转。
> 7. 参数 （ act_ru_task）：
> --------------------
> （父级 execution 变量）
> - nrOfInstances ：子任务总个数
> -  nrOfCompletedInstances: 当前已完成子任务个数
> - nrOfActiveInstances: 当前活跃任务个数（未完成）
> --------------------
>（子级 execution 变量）
> - loopCounter: 任务列表下标
> - assignee: 任务执行人（可以根据需要配置不同变量 XML）
> 

**实现思路：**
> ######会签 加签 
> **并行**：Activiti会基于父级 execution 创建多个子 execution  再根据子execution  创建多个任务，所以实现加签的时候，根据 父级 execution  和 节点 生成新的 execution  ，再生成任务。
>  
> **串行**：Activiti会基于 父级 execution 只创建一个 子execution，每完成一个任务，创建下一个任务。在开始串行会签任务前，需要传入一个变量 assigneeList，而这个变量会被 序列号 到 act_ru_task 中。在运行阶段，根据 loopCounter （数组下标），从assigneeList中获取 任务执行人。所以需要进行加签时，只需要传入列表并修改才行。
> 
> **注意：** 不论串行并行，在修改完任务之后，都需要修改父级变量计数器

测试流程图：
![测试流程](https://upload-images.jianshu.io/upload_images/14387783-69238c5b43f1937c.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

加签任务: com.oldguy.example.modules.workflow.commands.AddMultiInstanceExecutionCmd
```
public class AddMultiInstanceExecutionCmd extends AbstractCountersignCmd implements Command<String>, CountersigningVariables {

    /**
     * 当前任务ID
     */
    private String taskId;

    /**
     * 审核人
     */
    private List<String> assigneeList;

    /**
     * 任务执行人
     */
    private String assignee;

    public AddMultiInstanceExecutionCmd(String taskId, List<String> assigneeList) {

        super();

        if (ObjectUtils.isEmpty(assigneeList)) {
            throw new RuntimeException("assigneeList 不能为空!");
        }

        this.taskId = taskId;
        this.assigneeList = assigneeList;
    }

    public AddMultiInstanceExecutionCmd(String taskId, List<String> assigneeList, String assignee) {

        super();

        if (ObjectUtils.isEmpty(assigneeList)) {
            throw new RuntimeException("assigneeList 不能为空!");
        }

        this.taskId = taskId;
        this.assigneeList = assigneeList;
        this.assignee = assignee;
    }

    @Override
    public String execute(CommandContext commandContext) {

        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);

        UserTask userTask = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());

        if (userTask.getLoopCharacteristics() == null) {
            // TODO
            Log4jUtils.getInstance(getClass()).error("task:[" + task.getId() + "] 不是会签节任务");
        }

        /**
         *  获取父级
         */
        ExecutionEntityImpl parentNode = execution.getParent();

        /**
         *  获取流程变量
         */
        int nrOfInstances = (int) runtimeService.getVariable(parentNode.getId(), NUMBER_OF_INSTANCES);
        int nrOfActiveInstances = (int) runtimeService.getVariable(parentNode.getId(), NUMBER_OF_ACTIVE_INSTANCES);

        /**
         *  获取管理器
         */
        ExecutionEntityManager executionEntityManager = Context.getCommandContext().getExecutionEntityManager();

        Object behavior = userTask.getBehavior();
        if (behavior instanceof ParallelMultiInstanceBehavior) {

            Log4jUtils.getInstance(getClass()).info("task:[" + task.getId() + "] 并行会签 加签 任务");
            /**
             *  设置循环标志变量
             */
            runtimeService.setVariable(parentNode.getId(), NUMBER_OF_INSTANCES, nrOfInstances + assigneeList.size());
            runtimeService.setVariable(parentNode.getId(), NUMBER_OF_ACTIVE_INSTANCES, nrOfActiveInstances + assigneeList.size());

            /**
             *  新建任务列表
             */
            for (String assignee : this.assigneeList) {

                /**
                 *  创建 子 execution
                 */
                ExecutionEntity newExecution = executionEntityManager.createChildExecution(parentNode);

                newExecution.setActive(true);
                newExecution.setVariableLocal(LOOP_COUNTER, nrOfInstances);
                newExecution.setVariableLocal(ASSIGNEE_USER, assignee);
                newExecution.setCurrentFlowElement(userTask);

                /**
                 * 任务总数 +1
                 */
                nrOfInstances++;

                /**
                 * 推入时间表序列
                  */
                Context.getAgenda().planContinueMultiInstanceOperation(newExecution);
            }

        } else if (behavior instanceof SequentialMultiInstanceBehavior) {
            Log4jUtils.getInstance(getClass()).info("task:[" + task.getId() + "] 串行会签 加签 任务");

            /**
             *  是否需要替换审批人
             */
            boolean changeAssignee = false;
            if (StringUtils.isEmpty(assignee)) {
                assignee = task.getAssignee();
                changeAssignee = true;
            }
            /**
             *  当前任务执行位置
             */
            int loopCounterIndex = -1;

            for (int i = 0; i < assigneeList.size(); i++) {

                String temp = assigneeList.get(i);
                if (assignee.equals(temp)) {
                    loopCounterIndex = i;
                }
            }

            if (loopCounterIndex == -1) {
                throw new RuntimeException("任务审批人不存在于任务执行人列表中");
            }

            /**
             *  修改当前任务执行人
             */
            if (changeAssignee) {
                taskService.setAssignee(taskId, assignee);
                execution.setVariableLocal(ASSIGNEE_USER, assignee);
            }

            /**
             *  修改 计数器位置
             */
            execution.setVariableLocal(LOOP_COUNTER, loopCounterIndex);

            /**
             *  修改全局变量
             */
            Map<String, Object> variables = new HashMap<>(3);
            variables.put(NUMBER_OF_INSTANCES, assigneeList.size());
            variables.put(NUMBER_OF_COMPLETED_INSTANCES, loopCounterIndex);
            variables.put(ASSIGNEE_LIST, assigneeList);

            runtimeService.setVariables(parentNode.getId(), variables);
        }


        return "加签成功";
    }
}

```

> ######会签 减签 
> **并行**：根据上面的解释，同理，在进行会签减签的时候，只需要删除 相关的 子 execution 并且修改 父级计数器值就可以完成减签。
>  
> **串行**：串行减签与加签逻辑相似，只不过把加变成减而已
> 

减签：com.oldguy.example.modules.workflow.commands.DeleteMultiInstanceExecutionCmd
```
package com.oldguy.example.modules.workflow.commands;

import com.oldguy.example.modules.common.utils.Log4jUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.task.Task;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @ClassName: DeleteMultiInstanceExecutionCmd
 * @Author: ren
 * @Description: 进行会签减签 flowable:org.flowable.engine.impl.cmd.DeleteMultiInstanceExecutionCmd
 * @CreateTIme: 2019/5/9 0009 下午 3:05
 **/
public class DeleteMultiInstanceExecutionCmd extends AbstractCountersignCmd implements Command<String>, CountersigningVariables {

    /**
     * 当前任务ID
     */
    private String taskId;

    /**
     * 审核人
     */
    private List<String> assigneeList;

    public DeleteMultiInstanceExecutionCmd(String taskId, List<String> assigneeList) {

        super();

        if (ObjectUtils.isEmpty(assigneeList)) {
            throw new RuntimeException("assigneeList 不能为空!");
        }

        this.taskId = taskId;
        this.assigneeList = assigneeList;
    }

    @Override
    public String execute(CommandContext commandContext) {

        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);

        UserTask userTask = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());

        if (userTask.getLoopCharacteristics() == null) {
            // TODO
            Log4jUtils.getInstance(getClass()).error("task:[" + task.getId() + "] 不是会签节任务");
        }

        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        ExecutionEntityImpl parentNode = execution.getParent();

        /**
         *  获取任务完成数
         */
        int nrOfCompletedInstances = (int) runtimeService.getVariable(parentNode.getId(), NUMBER_OF_COMPLETED_INSTANCES);

        /**
         *  转换判断标识
         */
        Set<String> assigneeSet = new HashSet<>(assigneeList);
        ExecutionEntityManager executionEntityManager = Context.getCommandContext().getExecutionEntityManager();

        Object behavior = userTask.getBehavior();
        /**
         *  进行并行任务 减签
         */
        if (behavior instanceof ParallelMultiInstanceBehavior) {

            Log4jUtils.getInstance(getClass()).info("task:[" + task.getId() + "] 并行会签 减签 任务");

            /**
             *  当前任务列表
             */
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstance().getProcessInstanceId()).list();

            List<Task> removeTaskList = new ArrayList<>(assigneeSet.size());
            List<Task> existTaskList = new ArrayList<>(taskList.size() - assigneeSet.size());

            taskList.forEach(obj -> {

                if (assigneeSet.contains(obj.getAssignee())) {
                    removeTaskList.add(obj);

                    ExecutionEntityImpl temp = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(obj.getExecutionId()).singleResult();
                    executionEntityManager.deleteExecutionAndRelatedData(temp, "会签减签", true);

                } else {
                    existTaskList.add(obj);
                }
            });

            /**
             *  修改已完成任务变量,增加被删减任务
             */
            runtimeService.setVariable(parentNode.getId(), NUMBER_OF_COMPLETED_INSTANCES, nrOfCompletedInstances + removeTaskList.size());


        } else if (behavior instanceof SequentialMultiInstanceBehavior) {
            Log4jUtils.getInstance(getClass()).info("task:[" + task.getId() + "] 串行会签 减签 任务");

            Object obj = parentNode.getVariable(ASSIGNEE_LIST);
            if (obj == null || !(obj instanceof ArrayList)) {
                throw new RuntimeException("没有找到任务执行人列表");
            }


            ArrayList<String> sourceAssigneeList = (ArrayList) obj;
            List<String> newAssigneeList = new ArrayList<>();
            boolean flag = false;
            int loopCounterIndex = -1;
            String newAssignee = "";
            for (String temp : sourceAssigneeList) {
                if (!assigneeSet.contains(temp)) {
                    newAssigneeList.add(temp);
                }

                if (flag) {
                    newAssignee = temp;
                    flag = false;
                }

                if (temp.equals(task.getAssignee())) {

                    if (assigneeSet.contains(temp)) {
                        flag = true;
                        loopCounterIndex = newAssigneeList.size();
                    } else {
                        loopCounterIndex = newAssigneeList.size() - 1;
                    }
                }
            }

            /**
             *  修改计数器变量
             */
            Map<String, Object> variables = new HashMap<>();
            variables.put(NUMBER_OF_INSTANCES, newAssigneeList.size());
            variables.put(NUMBER_OF_COMPLETED_INSTANCES, loopCounterIndex > 0 ? loopCounterIndex - 1 : 0);
            variables.put(ASSIGNEE_LIST, newAssigneeList);
            runtimeService.setVariables(parentNode.getId(), variables);

            /**
             *  当前任务需要被删除，需要替换下一个任务审批人
             */
            if (!StringUtils.isEmpty(newAssignee)) {
                taskService.setAssignee(taskId, newAssignee);
                execution.setVariable(LOOP_COUNTER, loopCounterIndex);
                execution.setVariable(ASSIGNEE_USER, newAssignee);
            }
        }
        return "减签成功";
    }
}

```

######其他代码
抽象父类: com.oldguy.example.modules.workflow.commands.AbstractCountersignCmd
```
package com.oldguy.example.modules.workflow.commands;

import com.oldguy.example.modules.common.utils.SpringContextUtils;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;


/**
 * @ClassName: AbstractCountersignCmd
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/13 0013 下午 11:43
 **/
public abstract class AbstractCountersignCmd {

    protected RuntimeService runtimeService;

    protected TaskService taskService;

    protected RepositoryService repositoryService;

    public AbstractCountersignCmd(){

        runtimeService = SpringContextUtils.getBean(RuntimeService.class);
        taskService = SpringContextUtils.getBean(TaskService.class);
        repositoryService = SpringContextUtils.getBean(RepositoryService.class);
    }

}
```
通用接口参数: com.oldguy.example.modules.workflow.commands.CountersigningVariables
```
package com.oldguy.example.modules.workflow.commands;

import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;

/**
 *  Activiti 会签任务中变量标志
 *
 * {@link MultiInstanceActivityBehavior}
 */
public interface CountersigningVariables {

    /**
     *  默认审核人
     */
    String ASSIGNEE_USER = "assignee";

    /**
     *  审核人集合
     */
    String ASSIGNEE_LIST = "assigneeList";

    /**
     *  会签任务总数
     */
    String NUMBER_OF_INSTANCES = "nrOfInstances";

    /**
     *  正在执行的会签总数
     */
    String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";

    /**
     *  已完成的会签任务总数
     */
    String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";

    /**
     *  会签任务表示
     *  collectionElementIndexVariable
     */
    String LOOP_COUNTER = "loopCounter";
}

```