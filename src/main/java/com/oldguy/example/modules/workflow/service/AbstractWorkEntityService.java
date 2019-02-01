package com.oldguy.example.modules.workflow.service;


import com.oldguy.example.modules.common.dao.jpas.WorkEntityMapper;
import com.oldguy.example.modules.common.exceptions.FormValidException;
import com.oldguy.example.modules.common.utils.SpringContextUtils;
import com.oldguy.example.modules.workflow.configs.WorkFlowConfiguration;
import com.oldguy.example.modules.workflow.dto.TaskEntityInfo;
import com.oldguy.example.modules.workflow.dto.WorkBtn;
import com.oldguy.example.modules.workflow.dto.WorkEntityInfo;
import com.oldguy.example.modules.workflow.service.entities.ProcessAuditStatusService;
import com.oldguy.example.modules.workflow.service.entities.ProcessTaskConfigService;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.RuntimeService;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author huangrenhao
 * @date 2018/12/6
 */
public abstract class AbstractWorkEntityService {

    /**
     * 工作流实体集合
     */
    protected Map<String, AbstractProcessInstanceService> processEntityService = Collections.emptyMap();

    /**
     * DAO Mapper
     */
    protected Map<String, WorkEntityMapper> workEntityMapperMap = Collections.emptyMap();

    protected UserTaskService userTaskService;

    protected ProcessService processService;

    protected RuntimeService runtimeService;

    protected ProcessTaskConfigService processTaskConfigService;

    protected ProcessAuditStatusService processAuditStatusService;

    /**
     * 获取抽象类型的泛型类
     *
     * @param object
     * @return
     */
    public static Class getActualTypeArgumentByClassAbstractClass(Object object) {
        Class typeClass = (Class) ((ParameterizedType) object.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return typeClass;
    }

    /**
     * 获取接口的泛型类型
     *
     * @param object
     * @return
     */
    public static Class getActualTypeArgumentByClassInterface(Object object) {

        Type[] types = object.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[0];
        Class typeClass = (Class) parameterizedType.getActualTypeArguments()[0];
        return typeClass;
    }

    /**
     * 获取Mapper接口上的实现泛型
     *
     * @param object
     * @return
     */
    public static Class getActualTypeArgumentByInterface(Object object) {

        List<Class<?>> list = ClassUtils.getAllInterfaces(object.getClass());

        Class clazz = list.get(0);
        Type[] types = clazz.getGenericInterfaces();
        Class typeClass = (Class) ((ParameterizedType) types[0]).getActualTypeArguments()[0];

        return typeClass;
    }

    protected void init() {
        if (null == userTaskService) {
            userTaskService = SpringContextUtils.getBean(UserTaskService.class);
        }
        if (null == processService) {
            processService = SpringContextUtils.getBean(ProcessService.class);
        }
        if (null == runtimeService) {
            runtimeService = SpringContextUtils.getBean(RuntimeService.class);
        }
        if (null == processTaskConfigService) {
            processTaskConfigService = SpringContextUtils.getBean(ProcessTaskConfigService.class);
        }
        if (null == processAuditStatusService) {
            processAuditStatusService = SpringContextUtils.getBean(ProcessAuditStatusService.class);
        }

        // 注入DAO Mapper
        if (workEntityMapperMap.isEmpty()) {
            String[] baseEntityMapperNames = SpringContextUtils.getBeanNamesForType(WorkEntityMapper.class);
            workEntityMapperMap = new HashMap<>(baseEntityMapperNames.length);

            for (String name : baseEntityMapperNames) {
                WorkEntityMapper workEntityMapper = SpringContextUtils.getBean(name, WorkEntityMapper.class);

                // 获取返修类
                Class typeClass = getActualTypeArgumentByInterface(workEntityMapper);
                workEntityMapperMap.put(typeClass.getSimpleName(), workEntityMapper);
            }
        }

        // 注入ProcessInstanceService
        if (processEntityService.isEmpty()) {
            String[] workflowNames = SpringContextUtils.getBeanNamesForType(ProcessInstanceService.class);
            processEntityService = new HashMap<>(workflowNames.length);

            for (String name : workflowNames) {
                AbstractProcessInstanceService workFlow = SpringContextUtils.getBean(name, AbstractProcessInstanceService.class);

                // 获取返修类
                Class typeClass = getActualTypeArgumentByClassAbstractClass(workFlow);

                // 注入类别名称
                workFlow.setClassName(typeClass.getSimpleName());
                workFlow.setWorkEntityMapper(workEntityMapperMap.get(typeClass.getSimpleName()));
                workFlow.setProcessTaskConfigService(processTaskConfigService);
                workFlow.setProcessAuditStatusService(processAuditStatusService);

                processEntityService.put(typeClass.getSimpleName(), workFlow);
            }
        }
    }

    /**
     * 获取当前任务详情
     *
     * @param taskId
     * @return
     */
    public WorkEntityInfo getWorkEntityInfo(String taskId) {

        // 获取任务详情
        TaskEntityInfo task = userTaskService.currentTaskInfo(taskId);

        WorkEntityInfo entityInfo = new WorkEntityInfo();
        entityInfo.setTaskEntityInfo(task);
        // 获取批注
        entityInfo.setTaskComments(userTaskService.getComments(task.getProcessInstanceId()));
        // 获取输出路线
        List<SequenceFlow> outPutLinks = processService.getOutputLinkList(task.getProcessInstanceId(), task.getTaskDefinitionKey());


        ProcessInstanceService processInstanceService = processEntityService.get(task.getProcessDefinitionKey());
        if (null == processInstanceService) {
            throw new FormValidException("没有找到业务服务类:[ " + task.getProcessDefinitionKey() + " ]");
        }

        // 获取任务节点按钮
        List<WorkBtn> workBtnList = processInstanceService.getWorkBtnList(task.getProcessDefinitionId(), task.getTaskDefinitionKey(), outPutLinks);
        entityInfo.setWorkBtnList(workBtnList);

        // 注入业务实例对象
        entityInfo.setTarget(processInstanceService.getTarget(task.getEntityId()));

        return entityInfo;
    }

    /**
     * 更新流程状态
     *
     * @param businessKey
     * @param auditCode
     * @return
     */
    public int updateAuditStatus(String businessKey, String auditCode) {

        Map<String, String> map = WorkFlowConfiguration.trainFormBusinessKey(businessKey);
        String processDefinitionKey = map.get(WorkFlowConfiguration.PROCESS_DEFINITION_KEY);
        Long id = Long.valueOf(map.get(WorkFlowConfiguration.PROCESS_INSTANCE_ID));

        ProcessInstanceService processInstanceService = processEntityService.get(processDefinitionKey);
        if (null != processInstanceService) {
            return processInstanceService.updateAuditStatus(id, auditCode);
        }

        return 0;
    }
}
