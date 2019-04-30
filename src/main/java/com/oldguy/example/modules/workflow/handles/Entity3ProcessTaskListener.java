package com.oldguy.example.modules.workflow.handles;

import com.oldguy.example.modules.common.utils.SpringContextUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @ClassName: Entity3ProcessTaskListener
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/4/30 0030 上午 8:54
 **/
public class Entity3ProcessTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {

        RepositoryService repositoryService = SpringContextUtils.getBean(RepositoryService.class);

//        BpmnModel bpmnModel = repositoryService.getBpmnModel(delegateTask.getProcessDefinitionId());
//        Process process = bpmnModel.getProcesses().get(0);
//
//        // 当前任务节点
//        UserTask userTask = (UserTask) process.getFlowElement(delegateTask.getTaskDefinitionKey());

        System.out.println("assignee:" + delegateTask.getAssignee());

        System.out.println("------------------------------------");
    }
}
