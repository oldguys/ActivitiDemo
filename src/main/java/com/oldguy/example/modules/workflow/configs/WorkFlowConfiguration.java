package com.oldguy.example.modules.workflow.configs;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangrenhao
 * @date 2019/1/18
 */
public class WorkFlowConfiguration {

    /**
     * 工作流 执行用户
     */
    public static final String DEFAULT_USER_TASK_ASSIGNEE = "assignee";

    public static final String DEFAULT_USER_TASK_ASSIGNEE_LIST = "assigneeList";

    /**
     * businessKey 分割符
     */
    public final static String BUSINESS_KEY_SEPARATOR = "\\.";

    /**
     * businessKey 尺寸
     */
    public static final int BUSINESS_KEY_SIZE = 2;

    /***
     *  删除流程
     */
    public static final String DELETE_PROCESS_INSTANCE_FLAG = "delete";

    /**
     *  流程信息
     */
    public static final String PROCESS_DEFINITION_KEY = "PROCESS_DEFINITION_KEY";

    /**
     *  流程实例对应的业务ID
     */
    public static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";

    /**
     *  流程标示
     */
    public static final String WORKFLOW_FLOW_FLAG = "flowFlag";

    public static final String DEFAULT_SUBMIT = "default-submit";

    /**
     *  分割符
     */
    public static final String CANDIDATE_SEPARATOR = "\\,";

    public static final String PROCESS_END_EVENT_FLAG = "endevent";

    public static Map<String, String> trainFormBusinessKey(String businessKey) {

        if (StringUtils.isEmpty(businessKey)) {
            throw new RuntimeException("businessKey 不能为空!");
        }

        String[] params = businessKey.split(BUSINESS_KEY_SEPARATOR);
        if (params.length < BUSINESS_KEY_SIZE) {
            throw new RuntimeException("businessKey 格式不准确!");
        }

        Map<String, String> map = new HashMap<>(16);
        map.put(PROCESS_DEFINITION_KEY, params[0]);
        map.put(PROCESS_INSTANCE_ID, params[1]);

        return map;
    }
}
