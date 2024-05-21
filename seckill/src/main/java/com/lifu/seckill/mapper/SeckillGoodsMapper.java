package com.lifu.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifu.seckill.pojo.SeckillGoods;
import com.lifu.seckill.vo.GoodsVo;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lifu
 * @since 2024-05-20
 */
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    GoodsVo findGoodsVoBySeckillGoodsId(Long goodsId);
}
