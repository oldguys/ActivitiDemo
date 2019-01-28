package com.oldguy.example.modules.view.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author huangrenhao
 * @date 2019/1/19
 */
@Controller
@RequestMapping("view/sys")
public class SysController {

    @RequestMapping("UserEntity")
    public String index() {
        return "sys/UserEntity";
    }
}
