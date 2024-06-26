package com.lifu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifu.seckill.mapper.SeckillGoodsMapper;
import com.lifu.seckill.pojo.SeckillGoods;
import com.lifu.seckill.service.SeckillGoodsService;
import com.lifu.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public GoodsVo findGoodsVoBySeckillGoodsId(Long goodsId) {
        return seckillGoodsMapper.findGoodsVoBySeckillGoodsId(goodsId);
    }
}
