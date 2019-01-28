package com.oldguy.example.modules.test.dao.jpas;

import com.oldguy.example.modules.common.dao.jpas.BaseEntityMapper;
import com.oldguy.example.modules.common.dao.jpas.WorkEntityMapper;
import com.oldguy.example.modules.test.dao.entities.Entity2Process;
import org.springframework.stereotype.Repository;

/**
 * @author huangrenhao
 * @date 2019/1/18
 */
@Repository
public interface Entity2ProcessMapper extends WorkEntityMapper<Entity2Process> {

}
