package com.oldguy.example.modules.workflow.service;

import com.oldguy.example.modules.common.utils.Log4jUtils;
import com.oldguy.example.modules.workflow.dto.WorkEntityInfo;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author huangrenhao
 * @date 2019/1/21
 */
@Service
public class CommonWorkEntityService extends AbstractWorkEntityService {

    @PostConstruct
    public void initWorkFlowService() {
        Log4jUtils.getInstance(getClass()).info("初始化工作流服务----------------------");
        init();
    }

}
