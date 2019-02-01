package com.oldguy.example.modules.view.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author huangrenhao
 * @date 2019/1/19
 */
@Controller
@RequestMapping("view/test")
public class TestController {

    @RequestMapping("Entity1Process")
    public String Entity1Process() {
        return "test/Entity1Process";
    }

    @RequestMapping("Entity2Process")
    public String Entity2Process() {
        return "test/Entity2Process";
    }

    @RequestMapping("Entity3Process")
    public String Entity3Process() {
        return "test/Entity3Process";
    }
}
