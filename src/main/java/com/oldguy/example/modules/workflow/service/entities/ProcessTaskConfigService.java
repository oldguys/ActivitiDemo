package com.oldguy.example.modules.workflow.service.entities;

import com.oldguy.example.modules.common.dao.entities.BaseEntity;
import com.oldguy.example.modules.common.dao.jpas.BaseEntityMapper;
import com.oldguy.example.modules.common.utils.ReflectUtils;
import com.oldguy.example.modules.workflow.dao.entities.ProcessTaskConfig;
import com.oldguy.example.modules.workflow.dao.jpas.ProcessTaskConfigMapper;
import com.oldguy.example.modules.workflow.dto.form.ProcessTaskConfigForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Date: 2019/1/27 0027
 * @Author: ren
 * @Description:
 */
@Service
public class ProcessTaskConfigService {


    @Autowired
    private ProcessTaskConfigMapper processTaskConfigMapper;

    public Map<String, ProcessTaskConfig> getProcessTaskConfigMap(String processDefinitionKey, String processDefinitionId) {
        List<ProcessTaskConfig> records = processTaskConfigMapper.findByProcessDefinitionId(processDefinitionId);

        if(records.isEmpty()){
            records = processTaskConfigMapper.findByProcessDefinitionKey(processDefinitionKey);
        }

        Map<String, ProcessTaskConfig> recordMap = new HashMap<>(records.size());
        records.forEach(obj -> {
            recordMap.put(obj.getFlowId(), obj);
        });

        return recordMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public void persist(ProcessTaskConfigForm form) {

        Map<String, ProcessTaskConfig> recordMap = new HashMap<>();
        form.getElements().forEach(obj -> {

            ProcessTaskConfig entity = new ProcessTaskConfig();
            entity.setFlowFlag(obj.getFlowFlag());
            entity.setFlowId(obj.getFlowId());
            entity.setBtn(obj.getBtn());
            entity.setUrl(obj.getUrl());
            entity.setProcessDefinitionId(form.getProcessDefinitionId());
            entity.setProcessDefinitionKey(form.getProcessDefinitionKey());

            recordMap.put(obj.getFlowId(), entity);
        });


        List<ProcessTaskConfig> list = processTaskConfigMapper.findByProcessDefinitionId(form.getProcessDefinitionId());
        if (list.isEmpty()) {
            List<BaseEntity> entities = new ArrayList<>(recordMap.values());
            BaseEntityMapper.initNewInstance(entities);
            processTaskConfigMapper.saveBatch(recordMap.values());
            return;
        }

        list.forEach(obj -> {
            ProcessTaskConfig source = recordMap.get(obj.getFlowId());
            ReflectUtils.updateFieldByClass(source, obj);
        });
        processTaskConfigMapper.updateBatch(list);

    }
}
