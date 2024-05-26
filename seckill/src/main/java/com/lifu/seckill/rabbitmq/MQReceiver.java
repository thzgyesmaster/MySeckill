package com.lifu.seckill.rabbitmq;

import com.lifu.seckill.pojo.SeckillMessage;
import com.lifu.seckill.pojo.SeckillOrder;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.service.GoodsService;
import com.lifu.seckill.service.OrderService;
import com.lifu.seckill.utils.JSONUtil;
import com.lifu.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;


    private static final String QUEUE = "seckillQueue";


    @RabbitListener(queues = QUEUE)
    public void receive(String message){
        log.info("接收到的消息:" + message);

        SeckillMessage seckillMessage = JSONUtil.jsonStr2Object(message, SeckillMessage.class); //string转成SeckillMessage对象
        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodsId();

        //判断库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() < 1){
            return;
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.opsForValue().get("order:" + user.getId() + goodsId);

        if(seckillOrder != null){
            return;
        }
        //下单
        orderService.seckill(user , goodsVo);
    }


//    @RabbitListener(queues = "queue")
//    public void receive(Object msg){
//        log.info("接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg){
//        log.info("QUEUE01接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg){
//        log.info("QUEUE02接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg){
//        log.info("directQUEUE01接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg){
//        log.info("directQUEUE02接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void receive05(Object msg){
//        log.info("topicQUEUE01接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receive06(Object msg){
//        log.info("topicQUEUE02接收消息:" + msg);
//    }
}
