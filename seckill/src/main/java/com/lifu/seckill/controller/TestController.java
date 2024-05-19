package com.lifu.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("demo")
public class TestController {

    @RequestMapping("hello")
    public String hello(Model model){
        model.addAttribute("name","lifu");
        return "hello";
    }
}
