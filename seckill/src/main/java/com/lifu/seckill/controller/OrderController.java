package com.lifu.seckill.controller;


import com.lifu.seckill.pojo.User;
import com.lifu.seckill.service.OrderService;
import com.lifu.seckill.vo.OrderDetailVo;
import com.lifu.seckill.vo.RespBean;
import com.lifu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lifu
 * @since 2024-05-20
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user , Long orderId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        OrderDetailVo detail =  orderService.detail(orderId);
        return RespBean.success(detail);
    }
}
