package com.oldguy.example.modules.workflow.dao.entities;

import com.oldguy.example.modules.common.dao.entities.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @ClassName: TransformEntityInfo
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/5/23 0023 下午 2:58
 **/
@Entity
@Data
public class TransformEntityInfo extends BaseEntity {

    private String processInstanceId;

    private String taskDefineKey;

    /**
     *  是否串行：
     *  串行：true
     *  并行：false
     */
    private Boolean sequential;

}
