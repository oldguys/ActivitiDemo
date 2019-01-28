package com.oldguy.example.modules.common.dao.entities;


import java.util.Date;

/**
 * @author huangrenhao
 * @date 2018/11/27
 */
public class WorkFlowEntity extends BaseEntity {

    /**
     *  审批流程状态
     */
    private String auditStatus;

    /**
     *  创建人ID
     */
    private String creatorId;

    /**
     *  创建人姓名
     */
    private String creatorName;

    /**
     *  流程确认人ID
     */
    private String confirmUserId;

    /**
     *  流程确认人姓名
     */
    private String confirmUsername;

    /**
     *  流程确认时间
     */
    private Date confirmDate;

    /**
     *  备注
     */
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getConfirmUserId() {
        return confirmUserId;
    }

    public void setConfirmUserId(String confirmUserId) {
        this.confirmUserId = confirmUserId;
    }

    public String getConfirmUsername() {
        return confirmUsername;
    }

    public void setConfirmUsername(String confirmUsername) {
        this.confirmUsername = confirmUsername;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }
}
