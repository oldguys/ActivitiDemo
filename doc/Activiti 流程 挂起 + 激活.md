#Activiti 流程 挂起 + 激活

> 使用流程 挂起 + 激活
>
> 基于Activiti 6.0
> GitHub  [https://github.com/oldguys/ActivitiDemo](https://github.com/oldguys/ActivitiDemo)

![激活+挂起.png](https://upload-images.jianshu.io/upload_images/14387783-53fc0bb12ac8958d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 挂起
com.oldguy.example.modules.workflow.controllers.ProcessController
```
    /**
     *  挂起流程实例
     * @param processInstanceId
     * @return
     */
    @PostMapping("suspend/{processInstanceId}")
    public Object suspendProcess(@PathVariable("processInstanceId") String processInstanceId){
        processService.suspendProcessInstance(processInstanceId);
        return HttpJsonUtils.OK;
    }
```

com.oldguy.example.modules.workflow.service.ProcessService
```
    /**
     *  流程挂起
     * @param processInstanceId
     */
    public void suspendProcessInstance(String processInstanceId) {

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null == processInstance) {
            throw new FormValidException("不存在正在执行的流程实例:[ " + processInstanceId + " ]");
        }
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }
```

###### 激活
com.oldguy.example.modules.workflow.controllers.ProcessController
```
    /**
     *  激活流程实例
     * @param processInstanceId
     * @return
     */
    @PostMapping("activate/{processInstanceId}")
    public Object activateProcess(@PathVariable("processInstanceId") String processInstanceId){
        processService.activateProcessInstance(processInstanceId);
        return HttpJsonUtils.OK;
    }
```
com.oldguy.example.modules.workflow.service.ProcessService
```
    /**
     *  激活流程
     * @param processInstanceId
     */
    public void activateProcessInstance(String processInstanceId){
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null == processInstance) {
            throw new FormValidException("不存在正在执行的流程实例:[ " + processInstanceId + " ]");
        }
        runtimeService.activateProcessInstanceById(processInstanceId);
    }
```

获取当前用户 发起流程 SQL
```
    SELECT
        d.ID_ processInstanceId,
        d.BUSINESS_KEY_ businessKey,
        d.START_USER_ID_ creatorId,
        d.START_TIME_ startTime,
        d.END_TIME_ endTime,
        f.NAME_ processDefinitionName,
        f.KEY_ processDefinitionKey,
        e.NAME_ taskName,
        e.ASSIGNEE_ assignees,
        g.SUSPENSION_STATE_ active
    FROM
        act_hi_procinst d
    LEFT JOIN act_re_procdef f ON d.PROC_DEF_ID_ = f.ID_
    LEFT JOIN act_ru_execution g ON d.BUSINESS_KEY_ = g.BUSINESS_KEY_
    LEFT JOIN(
        SELECT
            a.NAME_,a.PROC_INST_ID_,
            IFNULL(a.ASSIGNEE_,GROUP_CONCAT(b.USER_ID_)) ASSIGNEE_
        FROM
            act_hi_taskinst a
        LEFT JOIN act_hi_identitylink b ON a.ID_ = b.TASK_ID_
        INNER JOIN (
        SELECT MAX(START_TIME_) START_TIME_ ,PROC_INST_ID_ FROM act_hi_taskinst WHERE ISNULL(END_TIME_) GROUP BY PROC_INST_ID_
        ) c ON a.START_TIME_ = c.START_TIME_ AND a.PROC_INST_ID_ = c.PROC_INST_ID_
        GROUP BY a.PROC_INST_ID_
    ) e ON d.ID_ = e.PROC_INST_ID_
    WHERE
        d.START_USER_ID_ = #{id};
```

