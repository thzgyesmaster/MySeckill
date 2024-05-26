package com.lifu.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String QUEUE = "seckillQueue";
    private static final String EXCHANGE = "seckillExchange";
    private static final String ROUTINGKEY = "seckill.#";

//    private static final String ROUTINGKEY01="queue.red";
//    private static final String ROUTINGKEY02="queue.green";
//    public void send(Object msg){
//        log.info("发送消息:" + msg);
//        rabbitTemplate.convertAndSend("fanoutExchange","",msg);
//    }
//
//    public void sendRed(Object msg){
//        log.info("发送消息red:" + msg);
//        rabbitTemplate.convertAndSend("directExchange",ROUTINGKEY01,msg);
//    }
//
//    public void sendGreen(Object msg){
//        log.info("发送消息green:" + msg);
//        rabbitTemplate.convertAndSend("directExchange",ROUTINGKEY02,msg);
//    }
//
//    public void send03(Object msg){
//        log.info("发送消息green:" + msg);
//        rabbitTemplate.convertAndSend("topicExchange","queue.message.kunkun",msg);
//    }
//
//    public void send04(Object msg){
//        log.info("发送消息green:" + msg);
//        rabbitTemplate.convertAndSend("topicExchange","jack.queue.mm",msg);
//    }

    /*
    发送秒杀信息
     */
    public void sendSeckillMessage(String message){
        log.info("发送信息:" + message);
        rabbitTemplate.convertAndSend(EXCHANGE,"seckill.message",message);
    }
}
