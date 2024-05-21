package com.lifu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifu.seckill.pojo.SeckillGoods;
import com.lifu.seckill.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lifu
 * @since 2024-05-20
 */
public interface SeckillGoodsService extends IService<SeckillGoods> {

    public GoodsVo findGoodsVoBySeckillGoodsId(Long goodsId);
}
