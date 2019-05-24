package com.oldguy.example.modules.workflow.service.activiti;

import java.util.Map;

/**
 *  特定业务完成类
 */
public interface MultiWorkFLow {

    /**
     *
     * @param taskId
     * @param data
     */
    void completeTask(String taskId, Map<String, Object> data);
}
