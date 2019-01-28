package com.oldguy.example.modules.workflow.controllers;

import com.oldguy.example.modules.common.utils.FormValidateUtils;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.workflow.dao.entities.ProcessAuditStatus;
import com.oldguy.example.modules.workflow.dao.jpas.ProcessAuditStatusMapper;
import com.oldguy.example.modules.workflow.dto.form.ProcessAuditStatusForm;
import com.oldguy.example.modules.workflow.service.entities.ProcessAuditStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author huangrenhao
 * @date 2019/1/23
 */
@RestController
@RequestMapping("ProcessAuditStatus")
public class ProcessAuditStatusController {

    @Autowired
    private ProcessAuditStatusMapper processAuditStatusMapper;
    @Autowired
    private ProcessAuditStatusService processAuditStatusService;

    @GetMapping("{ProcessDefinitionId}")
    public Object findByProcessDefinitionId(@PathVariable("ProcessDefinitionId") String ProcessDefinitionId){
        return processAuditStatusMapper.findByProcessDefinitionId(ProcessDefinitionId);
    }

    @PostMapping("persist")
    public Object persistProcessAuditStatus(@RequestBody ProcessAuditStatusForm form) {
        FormValidateUtils.validate(form, true);
        processAuditStatusService.persist(form);
        return HttpJsonUtils.OK;
    }

    @GetMapping("all")
    public List<ProcessAuditStatus> getList(Integer status) {
        return processAuditStatusMapper.findAllByStatus(status);
    }
}
