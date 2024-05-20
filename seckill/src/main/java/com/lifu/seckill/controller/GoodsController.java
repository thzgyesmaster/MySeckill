package com.lifu.seckill.controller;

import com.lifu.seckill.pojo.User;
import com.lifu.seckill.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

@Controller
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    private UserService userService;

    @RequestMapping("toList")
    public String toList(User user , Model model){

//        if(StringUtils.isEmpty(ticket)){
//            return "login";
//        }
//
//        //User user = (User) session.getAttribute(ticket);
//        User user = userService.getUserByCookie(ticket, request, response);
//        if(user == null){
//            return "login";
//        }


        model.addAttribute("user" , user);

        return "goodsList";
    }
}
