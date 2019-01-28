package com.oldguy.example.modules.workflow.dto;


import com.oldguy.example.modules.common.dto.AbstractPageQueryForm;
import com.oldguy.example.modules.common.dto.AbstractQueryForm;

/**
 * @author huangrenhao
 * @date 2018/12/26
 */
public class UserProcessInstanceQueryForm extends AbstractPageQueryForm {

    /**
     *  用户ID
     */
    private String userId;

    /**
     *  1:当前用户
     *  0:包含所有用户
     */
    private Integer current = 0;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
