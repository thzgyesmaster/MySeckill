package com.lifu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.vo.LoginVo;
import com.lifu.seckill.vo.RespBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lifu
 * @since 2024-05-19
 */
public interface UserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo);
}
