package com.oldguy.example.modules.test.controllers;

import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.test.dao.jpas.Entity5ProcessMapper;
import com.oldguy.example.modules.test.service.Entity5ProcessService;
import com.oldguy.example.modules.workflow.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: Entity4ProcessController
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/4/29 0029 上午 8:58
 **/
@RestController
@RequestMapping("Entity5Process")
public class Entity5ProcessController {


    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private Entity5ProcessMapper entity5ProcessMapper;
    @Autowired
    private Entity5ProcessService entity5ProcessService;

    /**
     * 开启流程实例
     *
     * @return
     */
    @PostMapping("openProcessInstance")
    public Object openProcessInstance() {
        String processInstanceId = entity5ProcessService.openProcessInstance();
        // 更新审核状态
        userTaskService.updateAuditStatus(processInstanceId);
        return HttpJsonUtils.OK;
    }


}
