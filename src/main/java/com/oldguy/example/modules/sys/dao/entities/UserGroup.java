package com.oldguy.example.modules.sys.dao.entities;

import com.oldguy.example.modules.common.dao.entities.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @Date: 2019/1/13 0013
 * @Author: ren
 * @Description:
 */
@Entity
public class UserGroup extends BaseEntity {

    private String groupSequence;

    private String groupName;

    public String getGroupSequence() {
        return groupSequence;
    }

    public void setGroupSequence(String groupSequence) {
        this.groupSequence = groupSequence;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
