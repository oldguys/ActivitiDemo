package com.oldguy.example.modules.workflow.controllers;

import com.oldguy.example.modules.sys.services.UserEntityService;
import com.oldguy.example.modules.workflow.dao.entities.HistoryTask;
import com.oldguy.example.modules.workflow.dao.jpas.HistoryTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author huangrenhao
 * @date 2019/1/19
 */
@RequestMapping("HistoryTask")
@RestController
public class HistoryTaskController {

    @Autowired
    private HistoryTaskMapper historyTaskMapper;


    @GetMapping("all")
    public List<HistoryTask> getList(Integer status) {
        return historyTaskMapper.findAllByStatus(status);
    }

    @GetMapping("current")
    public List<HistoryTask> current() {
        return historyTaskMapper.findByUserId(UserEntityService.getCurrentUserEntity().getUserId());
    }

}
