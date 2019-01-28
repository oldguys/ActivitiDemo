package com.oldguy.example.modules.workflow.dto;

/**
 * @author huangrenhao
 * @date 2018/12/6
 */
public class WorkBtn {

    /**
     *  按钮名称
     */
    private String name;

    /**
     *  路径
     */
    private String url;

    /**
     *  提交标示
     */
    private Object flowFlag;

    public WorkBtn(String name, String url, Object flowFlag) {
        this.name = name;
        this.url = url;
        this.flowFlag = flowFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getFlowFlag() {
        return flowFlag;
    }

    public void setFlowFlag(Object flowFlag) {
        this.flowFlag = flowFlag;
    }
}
