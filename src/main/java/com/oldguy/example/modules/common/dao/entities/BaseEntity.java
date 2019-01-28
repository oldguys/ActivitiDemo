package com.oldguy.example.modules.common.dao.entities;




import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @Description:
 * @Author: ren
 * @CreateTime: 2018-10-2018/10/15 0015 16:41
 */
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Date createTime;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
