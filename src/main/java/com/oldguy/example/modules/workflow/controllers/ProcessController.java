package com.oldguy.example.modules.workflow.controllers;

import com.baomidou.mybatisplus.plugins.Page;
import com.oldguy.example.modules.common.dto.BootstrapTablePage;
import com.oldguy.example.modules.common.exceptions.FormValidException;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.sys.services.UserEntityService;
import com.oldguy.example.modules.workflow.dao.entities.UserProcessInstance;
import com.oldguy.example.modules.workflow.dto.UserProcessInstanceQueryForm;
import com.oldguy.example.modules.workflow.service.ProcessService;
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
 * @date 2019/1/22
 */
@RestController
@RequestMapping("Process")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    /**
     *  激活流程实例
     * @param processInstanceId
     * @return
     */
    @PostMapping("activate/{processInstanceId}")
    public Object activateProcess(@PathVariable("processInstanceId") String processInstanceId){
        processService.activateProcessInstance(processInstanceId);
        return HttpJsonUtils.OK;
    }

    /**
     *  挂起流程实例
     * @param processInstanceId
     * @return
     */
    @PostMapping("suspend/{processInstanceId}")
    public Object suspendProcess(@PathVariable("processInstanceId") String processInstanceId){
        processService.suspendProcessInstance(processInstanceId);
        return HttpJsonUtils.OK;
    }

    @GetMapping("processDefinitions/info")
    public Object getProcessDefinitionInfo(String processDefinitionId){

        if(StringUtils.isBlank(processDefinitionId)){
            throw new FormValidException("processDefinitionId 流程定义ID不能为空!");
        }

        return processService.processDefinitionInfo(processDefinitionId);
    }

    @GetMapping("processDefinitions/list")
    public Object processDefinitions(){
        return processService.getProcessDefinitions();
    }

    @GetMapping("page")
    public Object page(UserProcessInstanceQueryForm form) {

        if (form.getCurrent() == 1) {
            form.setUserId(UserEntityService.getCurrentUserEntity().getUserId());
        }

        Page<UserProcessInstance> page = form.trainToPage();
        processService.findByPage(page, form);

        BootstrapTablePage<UserProcessInstance> result = new BootstrapTablePage();
        result.setRows(page.getRecords());
        result.setTotal(page.getTotal());
        return result;
    }

    /**
     * 获取流程定义图片
     *
     * @param processDefinitionId
     * @param response
     */
    @GetMapping("processDefinitionImage")
    public void processDefinitionImage(String processDefinitionId, HttpServletResponse response) throws IOException {

        if (StringUtils.isBlank(processDefinitionId)) {
            throw new FormValidException("processDefinitionId 不能为空!");
        }
        InputStream inputStream = processService.getDefinitionImage(processDefinitionId);
        OutputStream outputStream = response.getOutputStream();
        HttpUtils.copyImageStream(inputStream, outputStream);
    }
}
