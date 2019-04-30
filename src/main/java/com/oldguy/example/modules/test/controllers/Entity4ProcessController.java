package com.oldguy.example.modules.test.controllers;

import com.oldguy.example.modules.common.utils.FormValidateUtils;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.test.dao.jpas.Entity3ProcessMapper;
import com.oldguy.example.modules.test.dao.jpas.Entity4ProcessMapper;
import com.oldguy.example.modules.test.service.Entity3ProcessService;
import com.oldguy.example.modules.test.service.Entity4ProcessService;
import com.oldguy.example.modules.workflow.dto.TaskEntityInfo;
import com.oldguy.example.modules.workflow.dto.TaskForm;
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
@RequestMapping("Entity4Process")
public class Entity4ProcessController {


    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private Entity4ProcessMapper entity4ProcessMapper;
    @Autowired
    private Entity4ProcessService entity4ProcessService;

    /**
     * 开启流程实例
     *
     * @return
     */
    @PostMapping("openProcessInstance")
    public Object openProcessInstance() {
        String processInstanceId = entity4ProcessService.openProcessInstance();
        // 更新审核状态
        userTaskService.updateAuditStatus(processInstanceId);
        return HttpJsonUtils.OK;
    }


}
