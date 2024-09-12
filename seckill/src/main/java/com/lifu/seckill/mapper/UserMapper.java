package com.lifu.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifu.seckill.pojo.Register;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.vo.LoginVo;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lifu
 * @since 2024-05-19
 */
public interface UserMapper extends BaseMapper<User> {
    int insertUser(Register register);
}
