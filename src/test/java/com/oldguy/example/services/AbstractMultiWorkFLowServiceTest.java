package com.oldguy.example.services;

import com.oldguy.example.modules.common.dao.entities.WorkFlowEntity;
import com.oldguy.example.modules.common.dao.jpas.BaseMapper;
import com.oldguy.example.modules.common.dao.jpas.WorkEntityMapper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @ClassName: AbstractMultiWorkFLowServiceTest
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/24 0024 上午 8:55
 **/
public abstract class AbstractMultiWorkFLowServiceTest<T extends WorkFlowEntity> {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private WorkEntityMapper<T> entityMapper;

    protected String openProcessInstance(Class<T> clazz) throws Exception {
        WorkFlowEntity entity = newInstance(clazz);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(clazz.getSimpleName(), clazz.getSimpleName() + "." + entity.getId());
        return processInstance.getId();
    }


    protected <T> T newInstance(Class<T> clazz) throws Exception {

        WorkFlowEntity entity = (WorkFlowEntity) clazz.newInstance();

        entity.setCreateTime(new Date());
        entity.setAuditStatus("1");
        entity.setCreatorId("2");

        Method method = getMethod("save");
        method.invoke(entityMapper, entity);

        return clazz.cast(entity);

    }

    private Method getMethod(String methodName) {

        Method[] methods = BaseMapper.class.getDeclaredMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }

        return null;
    }

}
