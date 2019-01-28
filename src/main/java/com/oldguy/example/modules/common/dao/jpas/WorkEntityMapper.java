package com.oldguy.example.modules.common.dao.jpas;

import org.apache.ibatis.annotations.Param;

/**
 * @author huangrenhao
 * @date 2019/1/24
 */
public interface WorkEntityMapper<T> extends BaseEntityMapper<T> {

    /**
     *  更新流程状态
     * @param id
     * @param auditStatus
     * @return
     */
    int updateAuditStatus(@Param("id") Long id, @Param("auditStatus")  String auditStatus);
}
