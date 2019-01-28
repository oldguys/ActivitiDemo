package com.oldguy.example.modules.workflow.controllers;

import com.oldguy.example.modules.common.utils.FormValidateUtils;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.workflow.dao.entities.ProcessAuditStatus;
import com.oldguy.example.modules.workflow.dao.entities.ProcessTaskConfig;
import com.oldguy.example.modules.workflow.dao.jpas.ProcessTaskConfigMapper;
import com.oldguy.example.modules.workflow.dto.form.ProcessTaskConfigForm;
import com.oldguy.example.modules.workflow.service.entities.ProcessTaskConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Date: 2019/1/27 0027
 * @Author: ren
 * @Description:
 */
@RestController
@RequestMapping("ProcessTaskConfig")
public class ProcessTaskConfigController {


    @Autowired
    private ProcessTaskConfigService processTaskConfigService;
    @Autowired
    private ProcessTaskConfigMapper processTaskConfigMapper;

    @GetMapping("all")
    public List<ProcessTaskConfig> getList(Integer status) {
        return processTaskConfigMapper.findAllByStatus(status);
    }


    @PostMapping("persist")
    public Object persist(@RequestBody ProcessTaskConfigForm form){

        FormValidateUtils.validate(form,true);

        processTaskConfigService.persist(form);

        return HttpJsonUtils.OK;
    }
}
