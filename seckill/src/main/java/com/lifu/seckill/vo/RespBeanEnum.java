package com.lifu.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 公共返回对象枚举
 */

@Getter
@ToString
@AllArgsConstructor //全参构造
public enum RespBeanEnum {

    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),

    //登录模块
    LOGIN_ERROR(500210,"手机号或者密码不正确"),
    LOGIN_MOBILE_ERROR(500211,"手机号格式不正确"),
    LOGIN_MOBILE_NULL(500212,"手机号不存在"),
    LOGIN_PASSWORD_ERROR(500213,"密码错误");

    private final Integer code; //状态码
    private final String message;


}
