package com.lifu.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifu.seckill.pojo.Goods;
import com.lifu.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lifu
 * @since 2024-05-20
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
