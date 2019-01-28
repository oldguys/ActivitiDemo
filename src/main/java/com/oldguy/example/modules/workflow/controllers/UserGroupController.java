package com.oldguy.example.modules.workflow.controllers;

import com.oldguy.example.modules.sys.dao.entities.UserGroup;
import com.oldguy.example.modules.sys.dao.jpas.UserGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author huangrenhao
 * @date 2019/1/19
 */
@RestController
@RequestMapping("UserGroup")
public class UserGroupController {

    @Autowired
    private UserGroupMapper userGroupMapper;

    @GetMapping("all")
    public List<UserGroup> getList(Integer status) {
        return userGroupMapper.findAllByStatus(status);
    }

}
