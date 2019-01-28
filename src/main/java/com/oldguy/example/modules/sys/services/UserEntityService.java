package com.oldguy.example.modules.sys.services;

import com.oldguy.example.modules.sys.dao.entities.UserEntity;
import com.oldguy.example.modules.sys.dao.jpas.UserEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author huangrenhao
 * @date 2019/1/19
 */
@Service
public class UserEntityService {

    public static final String CURRENT_USER_FLAG = "CURRENT_USER";
    @Autowired
    private UserEntityMapper userEntityMapper;


    public static UserEntity getCurrentUserEntity() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Object obj = request.getSession().getAttribute(CURRENT_USER_FLAG);
        if (null != obj) {
            return (UserEntity) obj;
        }

        return null;
    }

    public Map<String,String> getUsernameMapByUserIds(Set<String> allUserIdSet) {
        // 关联ID对应的用户名
        List<UserEntity> userEntities = userEntityMapper.findByUserIdSet(allUserIdSet);
        Map<String, String> usernameMap = new HashMap<>(userEntities.size());
        userEntities.forEach(obj -> {
            usernameMap.put(obj.getUserId(), obj.getUsername());
        });

        return usernameMap;
    }
}
