package com.oldguy.example.modules.workflow.handles;


import com.oldguy.example.modules.common.utils.SpringContextUtils;
import com.oldguy.example.modules.sys.dao.jpas.UserGroupMapper;
import org.activiti.engine.delegate.TaskListener;

import java.util.Set;

/**
 * @author huangrenhao
 * @date 2018/12/19
 */
public abstract class AbstractProcessTaskListener implements TaskListener {


    protected Set<String> getAssigneeSet(String userGroupFlag) {
        UserGroupMapper userGroupMapper = SpringContextUtils.getBean(UserGroupMapper.class);
        Set<String> userIdSet = userGroupMapper.findUserIdByGroupSequence(userGroupFlag);
        if (userIdSet.isEmpty()) {
            throw new RuntimeException("找不到节点审批人!");
        }
        return userIdSet;
    }
}
