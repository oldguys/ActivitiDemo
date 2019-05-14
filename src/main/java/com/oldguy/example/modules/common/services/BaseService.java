package com.oldguy.example.modules.common.services;

import com.oldguy.example.modules.common.dao.entities.WorkFlowEntity;
import com.oldguy.example.modules.test.dao.entities.Entity5Process;
import com.oldguy.example.modules.test.dao.entities.Entity6Process;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: BaseService
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/6 0006 下午 12:13
 **/
public abstract class BaseService<T> {




    protected void newInstance(WorkFlowEntity entity){

        entity.setAuditStatus("1");
        entity.setCreatorId("1");
        entity.setCreateTime(new Date());
        entity.setStatus(1);
        entity.setCreatorName("测试用户1");

    }
}
