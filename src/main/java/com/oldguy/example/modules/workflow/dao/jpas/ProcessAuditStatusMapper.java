package com.oldguy.example.modules.workflow.dao.jpas;

import com.oldguy.example.modules.common.dao.jpas.BaseEntityMapper;
import com.oldguy.example.modules.workflow.dao.entities.ProcessAuditStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author huangrenhao
 * @date 2019/1/23
 */
@Repository
public interface ProcessAuditStatusMapper extends BaseEntityMapper<ProcessAuditStatus> {

    /**
     *  获取审核状态
     * @param processDefinitionId
     * @return
     */
    List<ProcessAuditStatus> findByProcessDefinitionId(String processDefinitionId);

    /**
     *  获取审核状态
     * @param processDefinitionId
     * @param userTask
     * @return
     */
    ProcessAuditStatus findByProcessDefinitionIdAndUserTask(@Param("processDefinitionId") String processDefinitionId,@Param("userTask")  String userTask);

    /**
     *
     * @param processDefinitionKey
     * @return
     */
    List<ProcessAuditStatus> findByProcessDefinitionKey(String processDefinitionKey);
}
