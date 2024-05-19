package com.lifu.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公共返回对象
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private long code;
    private String message;
    private Object obj; //返回时需要的对象

    //无返回对象 成功
    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode()
                ,RespBeanEnum.SUCCESS.getMessage(),null);
    }

    //有返回对象 成功
    public static RespBean success(Object obj){
        return new RespBean(RespBeanEnum.SUCCESS.getCode()
                ,RespBeanEnum.SUCCESS.getMessage(),obj);
    }

    //无返回对象,各种失败返回,根据respBeanEnum
    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCode(),
                respBeanEnum.getMessage(), null);
    }

    //有返回对象,各种失败返回,根据respBeanEnum
    public static RespBean error(RespBeanEnum respBeanEnum , Object obj){
        return new RespBean(respBeanEnum.getCode(),
                respBeanEnum.getMessage(), obj);
    }

}
