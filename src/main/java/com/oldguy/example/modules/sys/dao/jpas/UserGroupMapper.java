package com.oldguy.example.modules.sys.dao.jpas;

import com.oldguy.example.modules.common.dao.jpas.BaseEntityMapper;
import com.oldguy.example.modules.sys.dao.entities.UserGroup;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @Date: 2019/1/13 0013
 * @Author: ren
 * @Description:
 */
@Repository
public interface UserGroupMapper extends BaseEntityMapper<UserGroup> {

    /**
     *  获取角色组
     * @param groupSequence
     * @return
     */
    Set<String> findUserIdByGroupSequence(String groupSequence);
}
