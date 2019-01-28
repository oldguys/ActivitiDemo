package com.oldguy.example.modules.workflow.handles;

import com.oldguy.example.configs.DemoConfiguration;
import com.oldguy.example.modules.common.exceptions.FormValidException;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.commons.lang3.StringUtils;

/**
 * @Date: 2019/1/13 0013
 * @Author: ren
 * @Description:
 */
public class Entity2ProcessTaskListener extends AbstractProcessTaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {

        String formKey = delegateTask.getFormKey();
        if(StringUtils.isBlank(formKey)){
            throw new FormValidException("formKey 不能为空!");
        }
        delegateTask.addCandidateUsers(getAssigneeSet(formKey));
    }
}
