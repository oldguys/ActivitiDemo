package com.oldguy.example.modules.workflow.service.activiti.impls;

import com.oldguy.example.modules.workflow.service.activiti.DefaultInstanceConvertToMultiInstance;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.el.ExpressionManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: AbstractMultiWorkFLowService
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/23 0023 下午 4:55
 **/
public abstract class AbstractMultiWorkFLowService implements DefaultInstanceConvertToMultiInstance {

    @Autowired
    protected ProcessEngine processEngine;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected RepositoryService repositoryService;

    @Override
    public MultiInstanceLoopCharacteristics createMultiInstanceLoopCharacteristics(boolean isSequential) {
        return createMultiInstanceLoopCharacteristics(isSequential, DEFAULT_ASSIGNEE_LIST_EXP, ASSIGNEE_USER);
    }

    @Override
    public MultiInstanceLoopCharacteristics createMultiInstanceLoopCharacteristics(boolean isSequential, String assigneeListExp, String assignee) {

        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        multiInstanceLoopCharacteristics.setSequential(isSequential);
        multiInstanceLoopCharacteristics.setInputDataItem(assigneeListExp);
        multiInstanceLoopCharacteristics.setElementVariable(assignee);

        return multiInstanceLoopCharacteristics;
    }

    @Override
    public MultiInstanceActivityBehavior createMultiInstanceBehavior(UserTask userTask, boolean sequential) {
        return createMultiInstanceBehavior(userTask, sequential, DEFAULT_ASSIGNEE_LIST_EXP, ASSIGNEE_USER);
    }

    @Override
    public MultiInstanceActivityBehavior createMultiInstanceBehavior(UserTask userTask, boolean sequential, String assigneeListExp, String assignee) {


        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        /**
         *  创建解释器
         */
        UserTaskActivityBehavior userTaskActivityBehavior = processEngineConfiguration.getActivityBehaviorFactory().createUserTaskActivityBehavior(userTask);

        MultiInstanceActivityBehavior behavior = null;

        if (sequential) {
            behavior = new SequentialMultiInstanceBehavior(userTask, userTaskActivityBehavior);
        } else {
            behavior = new ParallelMultiInstanceBehavior(userTask, userTaskActivityBehavior);
        }

        /**
         *   注入表达式 解释器
         */
        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();

        /**
         * 设置表达式变量
         */
        behavior.setCollectionExpression(expressionManager.createExpression(assigneeListExp));
        behavior.setCollectionElementVariable(assignee);

        return behavior;
    }

}
