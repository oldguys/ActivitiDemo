package com.oldguy.example.modules.workflow.handles;

import com.oldguy.example.modules.common.exceptions.FormValidException;
import com.oldguy.example.modules.common.utils.SpringContextUtils;
import com.oldguy.example.modules.sys.dao.jpas.UserGroupMapper;
import de.odysseus.el.TreeValueExpression;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date: 2019/1/13 0013
 * @Author: ren
 * @Description:
 */
public class Entity4ProcessTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {

        System.out.println("assignee: "+delegateTask.getAssignee());
        System.out.println("-----------------------");

//        RepositoryService repositoryService = SpringContextUtils.getBean(RepositoryService.class);
//        RuntimeService runtimeService = SpringContextUtils.getBean(RuntimeService.class);
//        ProcessEngine processEngine = SpringContextUtils.getBean(ProcessEngine.class);
//
//
//        System.out.println("Id:" + delegateTask.getId());
//        System.out.println("name:" + delegateTask.getName());
//        System.out.println();
//
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(delegateTask.getProcessDefinitionId());
//        Process process = bpmnModel.getProcesses().get(0);
//
//        // 当前任务节点
//        UserTask userTask = (UserTask) process.getFlowElement(delegateTask.getTaskDefinitionKey());
//
//        System.out.println(userTask);
//
//        userTask.setAssignee("${assignee}");
//
//        // 多实例
//        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
//        multiInstanceLoopCharacteristics.setSequential(false);
////        multiInstanceLoopCharacteristics.setInputDataItem("1,2,3");
//        multiInstanceLoopCharacteristics.setInputDataItem("${assigneeList}");
//
////        multiInstanceLoopCharacteristics.setElementVariable("${assigneeList}");
//        multiInstanceLoopCharacteristics.setElementVariable("assignee");
//
//
//        // 注入循环控制
//        userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
//
//        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
//        // 创建任务实例
//        UserTaskActivityBehavior userTaskActivityBehavior = processEngineConfiguration.getActivityBehaviorFactory().createUserTaskActivityBehavior(userTask);
//        // 创建behavior
//
//        ParallelMultiInstanceBehavior behavior = new ParallelMultiInstanceBehavior(userTask, userTaskActivityBehavior);
//        userTask.setBehavior(behavior);
//
//        // 获取表达式解析工具
//        behavior.setCollectionElementVariable("assignee");
//
//        // 注入表达式
//        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();
//
//        System.out.println("expressionManager class :" + expressionManager.getClass().getName());
//
//        behavior.setCollectionExpression(expressionManager.createExpression("${assigneeList}"));
//
//
//        System.out.println("截断=================================================");
//
////        throw new RuntimeException("测试.................................");

    }


}
