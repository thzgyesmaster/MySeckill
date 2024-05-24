package com.lifu.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifu.seckill.exception.GlobalException;
import com.lifu.seckill.mapper.OrderMapper;
import com.lifu.seckill.pojo.Order;
import com.lifu.seckill.pojo.SeckillGoods;
import com.lifu.seckill.pojo.SeckillOrder;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.service.GoodsService;
import com.lifu.seckill.service.OrderService;
import com.lifu.seckill.service.SeckillGoodsService;
import com.lifu.seckill.service.SeckillOrderService;
import com.lifu.seckill.vo.GoodsVo;
import com.lifu.seckill.vo.OrderDetailVo;
import com.lifu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lifu
 * @since 2024-05-20
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private GoodsService goodsService;

    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId());
        SeckillGoods seckillGoods = seckillGoodsService.getOne(queryWrapper);

        //使秒杀商品库存数量减一
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        seckillGoodsService.updateById(seckillGoods);

        //建立订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //建立秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrderService.save(seckillOrder);

        return order;
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }

        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();

        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);

        return detail;
    }
}
