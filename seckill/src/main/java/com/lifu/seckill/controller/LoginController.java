package com.lifu.seckill.controller;

import com.lifu.seckill.service.UserService;
import com.lifu.seckill.vo.LoginVo;
import com.lifu.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 跳转登录页
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }


    /**
     * 登录功能
     * @param loginVo
     * @return
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        //log.info("{}",loginVo);

        return userService.doLogin(loginVo,response,request);
    }

    @RequestMapping("/toRegister")
    public String register(){
        System.out.println("进入后端注册界面");
        return "register";
    }

    @RequestMapping("/doRegister")
    @ResponseBody
    public RespBean doRegister(@Valid LoginVo loginVo, HttpServletRequest request , HttpServletResponse response){
        System.out.println("开始注册！");
        return  userService.doRegister(loginVo,request,response);
    }

}

