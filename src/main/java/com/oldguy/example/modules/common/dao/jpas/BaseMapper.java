package com.oldguy.example.modules.common.dao.jpas;/**
 * Created by Administrator on 2018/10/29 0029.
 */

import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * @Description:
 * @Author: ren
 * @CreateTime: 2018-10-2018/10/29 0029 10:19
 */
public interface BaseMapper<T, S> {

    /**
     *  批量持久化
     * @param collection
     * @return
     */
    int saveBatch(@Param("collections") Collection<T> collection);

    /**
     *  持久化单个
     * @param entity
     * @return
     */
    int save(T entity);

    /**
     *  更新单个
     * @param entity
     * @return
     */
    int update(T entity);
    /**
     *  获取实体
     * @param id
     * @return
     */
    T findOne(S id);
}
