package com.oldguy.example.modules.workflow.service.entities;

import com.oldguy.example.modules.common.utils.Log4jUtils;
import com.oldguy.example.modules.common.utils.ReflectUtils;
import com.oldguy.example.modules.workflow.dao.entities.ProcessAuditStatus;
import com.oldguy.example.modules.workflow.dao.jpas.ProcessAuditStatusMapper;
import com.oldguy.example.modules.workflow.dto.form.ProcessAuditStatusForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author huangrenhao
 * @date 2019/1/23
 */
@Service
public class ProcessAuditStatusService {

    @Autowired
    private ProcessAuditStatusMapper processAuditStatusMapper;

    public Map<String, ProcessAuditStatus> getMap(String processDefinitionKey,String processDefinitionId) {
        List<ProcessAuditStatus> records = processAuditStatusMapper.findByProcessDefinitionId(processDefinitionId);

        if(records.isEmpty()){
            records = processAuditStatusMapper.findByProcessDefinitionKey(processDefinitionKey);
        }

        Map<String, ProcessAuditStatus> resultMap = new HashMap<>(16);
        records.forEach(obj -> {
            resultMap.put(obj.getUserTask(), obj);
        });

        return resultMap;
    }

    /**
     * @param form
     */
    @Transactional(rollbackFor = Exception.class)
    public void persist(ProcessAuditStatusForm form) {

        List<ProcessAuditStatus> list = processAuditStatusMapper.findByProcessDefinitionId(form.getProcessDefinitionId());

        // 持久化
        if (list.isEmpty()) {
            List<ProcessAuditStatus> records = new ArrayList<>();
            List<ProcessAuditStatusForm.AuditItem> itemList = form.getElements();

            itemList.forEach(obj -> {
                ProcessAuditStatus item = new ProcessAuditStatus();
                item.setStatus(1);
                item.setCreateTime(new Date());
                item.setAuditCode(obj.getCode());
                item.setAuditMessage(obj.getMessage());
                item.setProcessDefinitionId(form.getProcessDefinitionId());
                item.setProcessDefinitionKey(form.getProcessDefinitionKey());
                item.setUserTask(obj.getItemId());
                records.add(item);
            });
            processAuditStatusMapper.saveBatch(records);
            return;
        }

        //
        Map<String, ProcessAuditStatus> map = new HashMap<>(16);
        List<ProcessAuditStatusForm.AuditItem> itemList = form.getElements();

        itemList.forEach(obj -> {
            ProcessAuditStatus item = new ProcessAuditStatus();
            item.setAuditCode(obj.getCode());
            item.setAuditMessage(obj.getMessage());
            item.setProcessDefinitionId(form.getProcessDefinitionId());
            item.setProcessDefinitionKey(form.getProcessDefinitionKey());
            item.setUserTask(obj.getItemId());
            map.put(obj.getItemId(), item);
        });

        list.forEach(obj -> {
            ProcessAuditStatus source = map.get(obj.getUserTask());
            if (source == null) {
                return;
            }
            ReflectUtils.updateFieldByClass(source, obj);
        });

        // 批量更新
        processAuditStatusMapper.updateBatch(list);
    }
}
