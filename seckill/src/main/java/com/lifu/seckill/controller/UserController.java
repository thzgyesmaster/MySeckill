package com.lifu.seckill.controller;


import com.lifu.seckill.pojo.User;
import com.lifu.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lifu
 * @since 2024-05-19
 */
@Controller
@RequestMapping("user")
public class UserController {

    /**
     * 用于用户登录压力测试
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping("info")
    public RespBean info(User user){
        return RespBean.success(user);
    }

}
