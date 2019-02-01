package com.oldguy.example.modules.view.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author huangrenhao
 * @date 2019/1/19
 */
@Controller
@RequestMapping("view/WorkFlow")
public class WorkFlowController {


    @RequestMapping("ProcessAuditStatus")
    public String ProcessAuditStatus() {
        return "workflow/ProcessAuditStatus";
    }

    @RequestMapping("ProcessTaskConfig")
    public String ProcessTaskConfig() {
        return "workflow/ProcessTaskConfig";
    }

    @RequestMapping("ProcessDefinitionConfig/{processDefinitionId}")
    public String ProcessDefinitionConfig() {
        return "workflow/process-definition-config";
    }

    @RequestMapping("ProcessDefinition")
    public String ProcessDefinition() {
        return "workflow/ProcessDefinition";
    }

    @RequestMapping("historyTask")
    public String historyTask() {
        return "workflow/history-task";
    }

    @RequestMapping("current")
    public String current() {
        return "workflow/current-task";
    }

    @RequestMapping("process")
    public String process() {
        return "workflow/process";
    }

    @RequestMapping("UserGroup")
    public String UserGroup() {
        return "workflow/UserGroup";
    }

    @RequestMapping("{processDefinitionKey}/{step}/{taskId}")
    public String taskInfo(@PathVariable("processDefinitionKey") String processDefinitionKey,
                           @PathVariable("step") String step) {
        return "workflow/task/Entity2Process-task";
//        return "workflow/task/" + processDefinitionKey + "-" + step;
    }

    @RequestMapping("Entity2Process/{taskId}")
    public String Entity2Process() {

        return "workflow/task/Entity2Process-task";
    }
}
