package com.lifu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifu.seckill.exception.GlobalException;
import com.lifu.seckill.mapper.UserMapper;
import com.lifu.seckill.pojo.Register;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.service.UserService;
import com.lifu.seckill.utils.CookieUtils;
import com.lifu.seckill.utils.MD5Util;
import com.lifu.seckill.utils.UUIDUtil;
import com.lifu.seckill.vo.LoginVo;
import com.lifu.seckill.vo.RespBean;
import com.lifu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lifu
 * @since 2024-05-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();


//        //无论前端是否校验,后端都要进行一次校验
//        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//
//        //判断手机号格式是否正确
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.LOGIN_MOBILE_ERROR);
//        }

        //根据数据库查Id是否存在
        User user = userMapper.selectById(mobile);
        if(user == null){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //校验密码
        if(!MD5Util.fromPassToDBPass(password,user.getSlat()).equals((user.getPassword()))){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //生成cookie
        String ticket = UUIDUtil.uuid();

        //request.getSession().setAttribute(ticket,user);

        //将 用户名+cookie:用户信息 存入redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);

        CookieUtils.setCookie(request,response,"userTicket",ticket); //设置cookie的值,并在response中返回cookie

        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket , HttpServletRequest request , HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicket)){
            return null;
        }

        User user = (User)redisTemplate.opsForValue().get("user:" + userTicket); //从redis中获取

        if(user != null){
            CookieUtils.setCookie(request,response,"userTicket",userTicket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if(user == null){
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }

        user.setPassword(MD5Util.inputPassToDBPass(password,user.getSlat()));
        int result = userMapper.updateById(user);
        if(result == 1){
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }

        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }

    @Override
    public RespBean doRegister(LoginVo loginVo, HttpServletRequest request , HttpServletResponse response) {

            return RespBean.success();

    }
}
