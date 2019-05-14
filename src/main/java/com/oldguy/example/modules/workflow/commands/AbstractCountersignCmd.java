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
