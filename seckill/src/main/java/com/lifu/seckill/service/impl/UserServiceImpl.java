package com.lifu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifu.seckill.mapper.UserMapper;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.service.UserService;
import com.lifu.seckill.utils.MD5Util;
import com.lifu.seckill.utils.ValidatorUtil;
import com.lifu.seckill.vo.LoginVo;
import com.lifu.seckill.vo.RespBean;
import com.lifu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

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

    @Override
    public RespBean doLogin(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();


        //无论前端是否校验,后端都要进行一次校验
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        //判断手机号格式是否正确
        if(!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.LOGIN_MOBILE_ERROR);
        }

        //根据数据库查手机号是否存在
        User user = userMapper.selectById(mobile);
        if(user == null){
            return RespBean.error(RespBeanEnum.LOGIN_MOBILE_NULL);
        }

        //校验密码
        System.out.println(password);
        if(!MD5Util.fromPassToDBPass(password,user.getSlat()).equals((user.getPassword()))){
            System.out.println(user.getSlat());
            System.out.println(MD5Util.fromPassToDBPass(password,user.getSlat()));
            return RespBean.error(RespBeanEnum.LOGIN_PASSWORD_ERROR);
        }

        return RespBean.success();
    }
}
