package com.lifu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifu.seckill.pojo.Order;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.vo.GoodsVo;
import com.lifu.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lifu
 * @since 2024-05-20
 */
public interface OrderService extends IService<Order> {
    Order seckill(User user , GoodsVo goodsVo);

    OrderDetailVo detail(Long orderId);
}
