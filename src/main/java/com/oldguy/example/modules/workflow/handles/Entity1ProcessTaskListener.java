package com.oldguy.example.modules.workflow.handles;

import com.oldguy.example.configs.DemoConfiguration;
import org.activiti.engine.delegate.DelegateTask;

/**
 * @Date: 2019/1/13 0013
 * @Author: ren
 * @Description:
 */
public class Entity1ProcessTaskListener extends AbstractProcessTaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.addCandidateUsers(getAssigneeSet(DemoConfiguration.WorkFlowConfig.ENTITY1_PROCESS));
    }
}
