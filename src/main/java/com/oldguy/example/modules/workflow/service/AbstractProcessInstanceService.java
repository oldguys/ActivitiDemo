package com.oldguy.example.modules.workflow.service;

import com.oldguy.example.modules.common.dao.jpas.WorkEntityMapper;
import com.oldguy.example.modules.workflow.dao.entities.ProcessAuditStatus;
import com.oldguy.example.modules.workflow.dao.entities.ProcessTaskConfig;
import com.oldguy.example.modules.workflow.dto.WorkBtn;
import com.oldguy.example.modules.workflow.service.entities.ProcessAuditStatusService;
import com.oldguy.example.modules.workflow.service.entities.ProcessTaskConfigService;
import org.activiti.bpmn.model.SequenceFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author huangrenhao
 * @date 2019/1/25
 */
public abstract class AbstractProcessInstanceService<T> implements ProcessInstanceService<T> {

    private WorkEntityMapper<T> workEntityMapper;

    private String className;

    private ProcessTaskConfigService processTaskConfigService;

    private ProcessAuditStatusService processAuditStatusService;

    public void setProcessTaskConfigService(ProcessTaskConfigService processTaskConfigService) {
        this.processTaskConfigService = processTaskConfigService;
    }

    public void setProcessAuditStatusService(ProcessAuditStatusService processAuditStatusService) {
        this.processAuditStatusService = processAuditStatusService;
    }

    public void setWorkEntityMapper(WorkEntityMapper workEntityMapper) {
        this.workEntityMapper = workEntityMapper;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public int updateAuditStatus(Long id, String auditCode) {
        return workEntityMapper.updateAuditStatus(id, auditCode);
    }

    @Override
    public T getTarget(Long id) {
        return workEntityMapper.findOne(id);
    }

    @Override
    public List<WorkBtn> getWorkBtnList(String processDefinitionId, String taskDefinitionKey, List<SequenceFlow> outPutLinks) {

        List<WorkBtn> list = new ArrayList<>(outPutLinks.size());
        Map<String, ProcessTaskConfig> processTaskConfigMap = processTaskConfigService.getProcessTaskConfigMap(className, processDefinitionId);

        outPutLinks.forEach(obj -> {

            ProcessTaskConfig processTaskConfig = processTaskConfigMap.get(obj.getId());
            if(null != processTaskConfig){
                list.add(new WorkBtn(processTaskConfig.getBtn(), processTaskConfig.getUrl(), processTaskConfig.getFlowFlag()));
            }
        });


        return list;
    }
}
