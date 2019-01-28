package com.oldguy.example.modules.sys.dao.jpas;

import com.oldguy.example.modules.common.dao.jpas.BaseEntityMapper;
import com.oldguy.example.modules.sys.dao.entities.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author huangrenhao
 * @date 2019/1/18
 */
@Repository
public interface UserEntityMapper extends BaseEntityMapper<UserEntity>{

    /**
     *  获取用户
     * @param userId
     * @return
     */
    UserEntity findByUserId(String userId);

    /**
     *  获取用户集合
     * @param allUserIdSet
     * @return
     */
    List<UserEntity> findByUserIdSet(@Param("collections") Collection<String> allUserIdSet);
}
