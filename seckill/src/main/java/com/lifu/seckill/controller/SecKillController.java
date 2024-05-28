package com.lifu.seckill.controller;

import com.lifu.seckill.config.AccessLimit;
import com.lifu.seckill.exception.GlobalException;
import com.lifu.seckill.pojo.SeckillMessage;
import com.lifu.seckill.pojo.SeckillOrder;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.rabbitmq.MQSender;
import com.lifu.seckill.service.GoodsService;
import com.lifu.seckill.service.OrderService;
import com.lifu.seckill.service.SeckillGoodsService;
import com.lifu.seckill.service.SeckillOrderService;
import com.lifu.seckill.utils.JSONUtil;
import com.lifu.seckill.vo.GoodsVo;
import com.lifu.seckill.vo.RespBean;
import com.lifu.seckill.vo.RespBeanEnum;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.lifu.seckill.vo.RespBeanEnum.SESSION_ERROR;

/**
 *
 */
@Slf4j
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

    @GetMapping("/captcha")
    public void captcha (User user , Long goodsId , HttpServletResponse response){
        if (user == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 设置请求头类型
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        SpecCaptcha  captcha = new SpecCaptcha(130, 32, 3);

        captcha.setCharType(Captcha.TYPE_NUM_AND_UPPER); //数字加字母

        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失效", e.getMessage());
        }
    }

    @AccessLimit(second=5,maxCount=5,needLogin=true)
    @GetMapping("/path")
    @ResponseBody
    public RespBean getPath(User user , Long goodsId , String captcha , HttpServletRequest request){
        if(user == null){
            return RespBean.error(SESSION_ERROR);
        }


        boolean check = orderService.checkCaptcha(user , goodsId , captcha);
        if(!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }

        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }



    @PostMapping(value = "/{path}/doSecKill")
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path , User user , Long goodsId){
        if(user == null){
            return RespBean.error(SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //第三次优化
        boolean check = orderService.checkPath(user,goodsId,path);
        //这个path有问题
//        if(!check){
//            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
//        }
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
