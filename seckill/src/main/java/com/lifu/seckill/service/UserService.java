package com.lifu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.vo.LoginVo;
import com.lifu.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lifu
 * @since 2024-05-19
 */
public interface UserService extends IService<User> {

    /**
     * 登录校验
     * @param loginVo
     * @param response
     * @param request
     * @return
     */
    RespBean doLogin(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request);

    /**
     * 根据cookie获取用户的值
     */
    User getUserByCookie(String userTicket , HttpServletRequest request , HttpServletResponse response);

    RespBean updatePassword(String userTicket , String password , HttpServletRequest request , HttpServletResponse response);

    RespBean doRegister(LoginVo loginVo, HttpServletRequest request , HttpServletResponse response);

}
