package com.oldguy.example.modules.workflow.controllers;

import com.oldguy.example.modules.common.exceptions.FormValidException;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.sys.services.UserEntityService;
import com.oldguy.example.modules.workflow.service.CommonWorkEntityService;
import com.oldguy.example.modules.workflow.service.UserTaskService;
import com.oldguy.example.modules.workflow.utils.HttpUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author huangrenhao
 * @date 2019/1/21
 */
@RestController
@RequestMapping("Task")
public class TaskController {

    @Autowired
    private UserTaskService userTaskService;
    @Autowired
    private CommonWorkEntityService commonWorkEntityService;

    @PostMapping("callBack/{taskId}")
    public Object callBack(@PathVariable("taskId") String taskId){

        userTaskService.callBack(taskId);

        return HttpJsonUtils.OK;
    }

    @GetMapping("ProcessInstanceImage/{taskId}")
    public void currentProcessInstanceImage(@PathVariable("taskId") String taskId, HttpServletResponse response) throws IOException {
        InputStream inputStream = userTaskService.currentProcessInstanceImage(taskId);
        OutputStream outputStream = response.getOutputStream();
        HttpUtils.copyImageStream(inputStream, outputStream);
    }

    @GetMapping("current/{taskId}")
    public Object taskInfo(@PathVariable("taskId") String taskId) {
        return commonWorkEntityService.getWorkEntityInfo(taskId);
    }

    /**
     * 获取当前用户列表
     *
     * @return
     */
    @GetMapping("current")
    public Object currentUserTask() {

        return userTaskService.currentTaskList(UserEntityService.getCurrentUserEntity().getUserId());
    }
}
