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
import com.lifu.seckill.vo.RespBean;
import com.lifu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.lifu.seckill.vo.RespBeanEnum.SESSION_ERROR;

/**
 *
 */
@Controller
@RequestMapping("/secKill")
public class SecKillController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/doSecKill")
    @ResponseBody
    public RespBean doSecKill(Model model , User user , Long goodsId){
        if(user == null){
            return RespBean.error(SESSION_ERROR);
        }

        //查看库存是否充足 --这里应该是秒杀商品吧
        GoodsVo goodsVo = seckillGoodsService.findGoodsVoBySeckillGoodsId(goodsId);

        if (goodsVo.getGoodsStock() < 1) {
            model.addAttribute("errmsg" , RespBeanEnum.EMPTY_STOCK.getMessage());
            //转跳秒杀失败页面
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //判断用户是否重复抢购
        Long userId = user.getId();
        QueryWrapper<SeckillOrder> seckillOrderQueryWrapper = new QueryWrapper<SeckillOrder>().eq("user_id", userId).eq("goods_id", goodsId);
        SeckillOrder seckillOrder = seckillOrderService.getOne(seckillOrderQueryWrapper);

        if(seckillOrder != null){
            model.addAttribute("errmsg" , RespBeanEnum.REPEATE_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //为什么这不是seckillOrderService??
        Order order = orderService.seckill(user,goodsVo);

        //转跳订单详情页面
        return RespBean.success(order);
    }
}
