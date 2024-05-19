package com.lifu.seckill.controller;

import com.lifu.seckill.service.UserService;
import com.lifu.seckill.vo.LoginVo;
import com.lifu.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 登录
     * @param loginVo
     * @return
     */
    @PostMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(LoginVo loginVo){
        return userService.doLogin(loginVo);
    }
}
