package com.oldguy.example.modules.workflow.dao.jpas;


import com.baomidou.mybatisplus.plugins.Page;
import com.oldguy.example.modules.workflow.dao.entities.UserProcessInstance;
import com.oldguy.example.modules.workflow.dto.UserProcessInstanceQueryForm;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author huangrenhao
 * @date 2018/12/26
 */
@Repository
public interface UserProcessInstanceMapper {

    /**
     * 获取 流程信息
     *
     * @param page
     * @param form
     * @return
     */
    List<UserProcessInstance> findByPage(Page<UserProcessInstance> page, @Param("form") UserProcessInstanceQueryForm form);

    /**
     * 获取流程实例
     * @param processInstanceId
     * @return
     */
    UserProcessInstance findOne(@Param("processInstanceId") String processInstanceId);
}
