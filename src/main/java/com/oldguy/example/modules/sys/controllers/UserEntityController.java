package com.oldguy.example.modules.sys.controllers;

import com.oldguy.example.modules.common.exceptions.FormValidException;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.sys.annonation.NoLoginPerm;
import com.oldguy.example.modules.sys.dao.entities.UserEntity;
import com.oldguy.example.modules.sys.dao.jpas.UserEntityMapper;
import com.oldguy.example.modules.sys.services.UserEntityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author huangrenhao
 * @date 2019/1/19
 */
@RestController
@RequestMapping("UserEntity")
public class UserEntityController {

    @Autowired
    private UserEntityMapper userEntityMapper;

    @GetMapping("all")
    public List<UserEntity> getList(Integer status) {
        return userEntityMapper.findAllByStatus(status);
    }

}
