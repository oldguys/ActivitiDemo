package com.oldguy.example.modules.workflow.dao.jpas;

import com.oldguy.example.modules.common.dao.jpas.BaseEntityMapper;
import com.oldguy.example.modules.workflow.dao.entities.HistoryTask;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author huangrenhao
 * @date 2019/1/21
 */
@Repository
public interface HistoryTaskMapper extends BaseEntityMapper {

    /**
     *  通过用户ID
     * @param userId
     * @return
     */
    List<HistoryTask> findByUserId(String userId);
}
