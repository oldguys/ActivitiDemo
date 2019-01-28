package com.oldguy.example.modules.workflow.service;



import com.oldguy.example.modules.workflow.dto.WorkBtn;
import org.activiti.bpmn.model.SequenceFlow;

import java.util.List;

/**
 * @author huangrenhao
 * @date 2018/12/5
 */
public interface ProcessInstanceService<T> {

    /**
     *  获取流程任务节点按钮
     * @param taskDefinitionKey
     * @param outPutLinks
     * @return
     */
    List<WorkBtn> getWorkBtnList(String processDefinitionId, String taskDefinitionKey, List<SequenceFlow> outPutLinks);

    /**
     * 获取流程实体
     *
     * @param id
     * @return
     */
    T getTarget(Long id);

    /**
     *  更新流程状态
     * @param id
     * @param auditCode
     * @return
     */
    int updateAuditStatus(Long id, String auditCode);
}
