package com.oldguy.example.modules.workflow.dao.jpas;

import com.oldguy.example.modules.common.dao.jpas.BaseEntityMapper;
import com.oldguy.example.modules.workflow.dao.entities.TransformEntityInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface TransformEntityInfoMapper extends BaseEntityMapper<TransformEntityInfo> {

    TransformEntityInfo findByProcessInstance(String processInstanceId);

}
