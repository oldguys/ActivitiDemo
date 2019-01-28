package com.oldguy.example.modules.test.dao.entities;

import com.oldguy.example.modules.common.dao.entities.WorkFlowEntity;

import javax.persistence.Entity;

/**
 * @author huangrenhao
 * @date 2019/1/18
 */
@Entity
public class Entity1Process extends WorkFlowEntity {

    public enum AuditStatus {
        Step1("1", "Step1"),
        Step2("2", "Step2"),
        Step3("3", "完成"),
        Step0("0", "流程被驳回");

        private String code;

        private String name;

        AuditStatus(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
