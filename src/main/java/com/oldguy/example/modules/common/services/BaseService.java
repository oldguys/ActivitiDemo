package com.oldguy.example.modules.common.services;

import com.oldguy.example.modules.common.dao.entities.WorkFlowEntity;

import java.util.Date;

/**
 * @ClassName: BaseService
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/6 0006 下午 12:13
 **/
public abstract class BaseService {


    protected void newInstance(WorkFlowEntity entity){

        entity.setAuditStatus("1");
        entity.setCreatorId("1");
        entity.setCreateTime(new Date());
        entity.setStatus(1);
        entity.setCreatorName("测试用户1");

    }
}
