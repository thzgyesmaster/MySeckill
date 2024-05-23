package com.lifu.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifu.seckill.pojo.SeckillOrder;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.pojo.Order;
import com.lifu.seckill.service.GoodsService;
import com.lifu.seckill.service.OrderService;
import com.lifu.seckill.service.SeckillGoodsService;
import com.lifu.seckill.service.SeckillOrderService;
import com.lifu.seckill.vo.GoodsVo;
import com.lifu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 *
 */
@Controller
@RequestMapping("secKill")
public class SecKillController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private OrderService orderService;

    @RequestMapping("doSecKill")
    public String doSecKill(Model model , User user , Long goodsId){
        if(user == null){
            return "login";
        }

        //查看库存是否充足 --这里应该是秒杀商品吧
        GoodsVo goodsVo = seckillGoodsService.findGoodsVoBySeckillGoodsId(goodsId);
        System.out.println(goodsVo);
        if (goodsVo.getGoodsStock() < 1) {
            model.addAttribute("errmsg" , RespBeanEnum.EMPTY_STOCK.getMessage());
            //转跳秒杀失败页面
            return "secKillFail";
        }

        //判断用户是否重复抢购
        Long userId = user.getId();
        QueryWrapper<SeckillOrder> seckillOrderQueryWrapper = new QueryWrapper<SeckillOrder>().eq("user_id", userId).eq("goods_id", goodsId);
        SeckillOrder seckillOrder = seckillOrderService.getOne(seckillOrderQueryWrapper);

        if(seckillOrder != null){
            model.addAttribute("errmsg" , RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }

        //为什么这不是seckillOrderService??
        Order order = orderService.seckill(user,goodsVo);
        model.addAttribute("order" , order);
        model.addAttribute("goods" , goodsVo);
        model.addAttribute("user" , user);

        //转跳订单详情页面
        return "orderDetail";
    }
}
