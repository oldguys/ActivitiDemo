package com.oldguy.example.modules.sys.dao.entities;

import com.oldguy.example.modules.common.dao.entities.BaseEntity;

import javax.persistence.Entity;

/**
 * @author huangrenhao
 * @date 2019/1/18
 */
@Entity
public class UserEntity extends BaseEntity {

    private String username;

    private String userId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
