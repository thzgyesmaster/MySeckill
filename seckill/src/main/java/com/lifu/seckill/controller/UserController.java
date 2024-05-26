package com.lifu.seckill.controller;


import com.lifu.seckill.pojo.User;
import com.lifu.seckill.rabbitmq.MQSender;
import com.lifu.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lifu
 * @since 2024-05-19
 */
@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    /**
     * 用于用户登录压力测试
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping("info")
    public RespBean info(User user){
        return RespBean.success(user);
    }


//    @RequestMapping("mq")
//    @ResponseBody
//    public void mq(){
//        mqSender.send("nihao,嘎嘎");
//    }
//
//    @RequestMapping("mq/fanout")
//    @ResponseBody
//    public void mq01(){
//        mqSender.send("666666666666666666666666666666");
//    }
//
//    @RequestMapping("mq/direct01")
//    @ResponseBody
//    public void mq02(){
//        mqSender.sendRed("direct,hello,red");
//    }
//
//    @RequestMapping("mq/direct02")
//    @ResponseBody
//    public void mq03(){
//        mqSender.sendGreen("direct,hello,green");
//    }
//
//    @RequestMapping("mq/topic01")
//    @ResponseBody
//    public void mq04(){
//        mqSender.send03("topic11");
//    }
//
//    @RequestMapping("mq/topic02")
//    @ResponseBody
//    public void mq05(){
//        mqSender.send04("topic22");
//    }
}
