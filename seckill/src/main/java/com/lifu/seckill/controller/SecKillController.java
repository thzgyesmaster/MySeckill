package com.lifu.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifu.seckill.pojo.SeckillMessage;
import com.lifu.seckill.pojo.SeckillOrder;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.pojo.Order;
import com.lifu.seckill.rabbitmq.MQSender;
import com.lifu.seckill.service.GoodsService;
import com.lifu.seckill.service.OrderService;
import com.lifu.seckill.service.SeckillGoodsService;
import com.lifu.seckill.service.SeckillOrderService;
import com.lifu.seckill.utils.JSONUtil;
import com.lifu.seckill.vo.GoodsVo;
import com.lifu.seckill.vo.RespBean;
import com.lifu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import static com.lifu.seckill.vo.RespBeanEnum.SESSION_ERROR;

/**
 *
 */
@Controller
@RequestMapping("/secKill")
public class SecKillController implements InitializingBean {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private RedisScript redisScript;

    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception { //init 在bean的属性初始化后都会执行该方法
        List<GoodsVo> goodsList = goodsService.findGoodsVo();
        if(goodsList == null){
            return ;
        }
        goodsList.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(),goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId() , false);
        });
    }

    @PostMapping("/doSecKill")
    @ResponseBody
    public RespBean doSecKill(User user , Long goodsId){
        if(user == null){
            return RespBean.error(SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //第二次优化

        //防止一个用户超买
        SeckillOrder seckillOrder = (SeckillOrder)valueOperations.get("order:" + user.getId() + goodsId);
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //内存标记,减少redis访问
        if(EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //预减库存
       // Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        //redis分布式锁
        Long stock = (Long) redisTemplate.execute(redisScript,
                Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        //判断redis中的商品数量-1后 是否<0
        if(stock < 0){
            EmptyStockMap.put(goodsId ,true);
//            valueOperations.increment("seckillGoods:" + goodsId); //使redis数据不为负数
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //数据库操作
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JSONUtil.object2JsonStr(seckillMessage)); //将SeckillMessage对象转成String

        //转跳订单详情页面
        return RespBean.success(0);

        //第一次优化
        /*
            //查看库存是否充足 --这里应该是秒杀商品吧
        GoodsVo goodsVo = seckillGoodsService.findGoodsVoBySeckillGoodsId(goodsId);

        if (goodsVo.getGoodsStock() < 1) {
            //model.addAttribute("errmsg" , RespBeanEnum.EMPTY_STOCK.getMessage());
            //转跳秒杀失败页面
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //判断用户是否重复抢购
        Long userId = user.getId();
        //初始代码
        QueryWrapper<SeckillOrder> seckillOrderQueryWrapper = new QueryWrapper<SeckillOrder>().eq("user_id", userId).eq("goods_id", goodsId);
        SeckillOrder seckillOrder = seckillOrderService.getOne(seckillOrderQueryWrapper);
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.opsForValue().get("order:" + userId + goodsId);

        if(seckillOrder != null){
            //model.addAttribute("errmsg" , RespBeanEnum.REPEATE_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //为什么这不是seckillOrderService??
        Order order = orderService.seckill(user,goodsVo);

        //转跳订单详情页面
        return RespBean.success(order);
         */

    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId: 成功  -1:秒杀失败  0：排队中
     */
    @GetMapping(value = "/result")
    @ResponseBody
    public RespBean getResult(User user , Long goodsId){
        if(user == null){
            return RespBean.error(SESSION_ERROR);
        }

        Long orderId = seckillOrderService.getResult(user , goodsId);
        return RespBean.success(orderId);
    }

}
