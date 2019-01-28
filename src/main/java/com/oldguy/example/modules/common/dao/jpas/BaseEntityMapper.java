package com.oldguy.example.modules.common.dao.jpas;

import com.oldguy.example.modules.common.dao.entities.BaseEntity;
import com.oldguy.example.modules.workflow.dao.entities.ProcessTaskConfig;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 实体映射基类，用于映射BaseEntity的子类
 *
 * @param <T>
 * @author huangrenhao
 * @version V1.0
 * @ClassName: BaseEntityMapper
 * @Description: TODO
 * @date 2017年12月4日 上午10:44:55
 */
public interface BaseEntityMapper<T> extends BaseMapper<T, Long> {

    /**
     *  初始化新实例
     * @param collection
     */
    static void initNewInstance(Collection<BaseEntity> collection) {
        collection.forEach(obj -> {
            obj.setStatus(1);
            obj.setCreateTime(new Date());
        });
    }


    /**
     * 获取 List<T> 列表
     * status :
     * 1 - 有效
     * 0 - 无效
     * null -> 所有
     *
     * @param status
     * @return
     */
    List<T> findAllByStatus(@Param("status") Integer status);

    /**
     * 修改 T 状态
     *
     * @param id
     * @param status
     * @return
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     *  批量更新实体
     * @param collection
     * @return
     */
    int updateBatch(@Param("collection") Collection<T> collection);
}
