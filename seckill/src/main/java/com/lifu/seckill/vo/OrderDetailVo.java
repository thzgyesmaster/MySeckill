package com.lifu.seckill.vo;

import com.lifu.seckill.pojo.Order;
import lombok.Data;

@Data
public class OrderDetailVo {
    private Order order;
    private GoodsVo goodsVo;
}
