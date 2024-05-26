package com.lifu.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifu.seckill.mapper.SeckillOrderMapper;
import com.lifu.seckill.pojo.SeckillOrder;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lifu
 * @since 2024-05-20
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Long getResult(User user, Long goodsId) {
        QueryWrapper<SeckillOrder> queryWrapper = new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId()).eq("goods_id", goodsId);

        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(queryWrapper);

        if(seckillOrder != null){
            return seckillOrder.getOrderId();
        }else if(redisTemplate.hasKey("isStockEmpty:" + goodsId)){
            return -1L;
        }else{
            return 0L;
        }

    }
}
