package com.oldguy.example.modules.sys.dao.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author huangrenhao
 * @date 2019/1/24
 */
@Entity
@Data
public class UserAndGroup {

    @GeneratedValue
    @Id
    private Long id;

    private String userId;

    private String groupSequence;
}
