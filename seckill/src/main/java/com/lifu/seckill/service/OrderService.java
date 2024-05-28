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
    /**
     * 秒杀并产生订单
     * @param user
     * @param goodsVo
     * @return
     */
    Order seckill(User user , GoodsVo goodsVo);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId , String path);

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
