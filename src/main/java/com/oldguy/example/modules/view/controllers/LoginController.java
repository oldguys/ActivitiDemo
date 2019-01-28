package com.oldguy.example.modules.view.controllers;

import com.oldguy.example.modules.common.exceptions.FormValidException;
import com.oldguy.example.modules.common.utils.HttpJsonUtils;
import com.oldguy.example.modules.sys.annonation.NoLoginPerm;
import com.oldguy.example.modules.sys.dao.entities.UserEntity;
import com.oldguy.example.modules.sys.dao.jpas.UserEntityMapper;
import com.oldguy.example.modules.sys.services.UserEntityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @author huangrenhao
 * @date 2019/1/19
 */
@Controller
public class LoginController {

    @Autowired
    private UserEntityMapper userEntityMapper;

    @RequestMapping({"", "login"})
    public String login() {
        return "login";
    }

    @NoLoginPerm
    @PostMapping("sign")
    public String sign(HttpSession session, Model model, String userId) {

        if (StringUtils.isBlank(userId)) {
            model.addAttribute("errorMessage","用户ID 不能为空!");
            return "/login";
        }

        UserEntity userEntity = userEntityMapper.findByUserId(userId);

        if (null == userEntity) {
            model.addAttribute("errorMessage","不存在该用户!");
            return "/login";
        }
        session.setAttribute(UserEntityService.CURRENT_USER_FLAG, userEntity);
        return "redirect:view/sys/UserEntity";
    }
}
