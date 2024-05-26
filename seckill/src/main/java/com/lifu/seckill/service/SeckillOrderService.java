package com.lifu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifu.seckill.pojo.SeckillOrder;
import com.lifu.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lifu
 * @since 2024-05-20
 */
public interface SeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId: 成功  -1:秒杀失败  0：排队中
     */
    Long getResult(User user, Long goodsId);
}
