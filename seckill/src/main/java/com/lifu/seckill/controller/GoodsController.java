package com.lifu.seckill.controller;

import com.lifu.seckill.pojo.User;
import com.lifu.seckill.service.GoodsService;
import com.lifu.seckill.service.UserService;
import com.lifu.seckill.vo.DetailVo;
import com.lifu.seckill.vo.GoodsVo;


import com.lifu.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8" )
    @ResponseBody
    public String toList(User user , Model model , HttpServletRequest request , HttpServletResponse response) {

        //如果从redis中获取的页面不为空,则直接返回页面
        String html = (String)redisTemplate.opsForValue().get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user" , user);
        model.addAttribute("goodsList" , goodsService.findGoodsVo() );

        //如果为空,手动渲染,存入redis并返回
        //获得模板引擎
//        Context context = new Context(request.getLocale(), model.asMap());
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());


        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);//thymeleaf模板引擎渲染页面
        if(!StringUtils.isEmpty(html)){
            redisTemplate.opsForValue().set("goodsList",html,60, TimeUnit.SECONDS); //要设置页面的失效时间
        }

//        return "goodsList";
        return html;
    }

    /**
     * 跳转商品详情
     * @param goodsId
     * @return
     */
    @RequestMapping( "/detail/{goodsId}")
    @ResponseBody
    public RespBean detail(@PathVariable Long goodsId , User user){

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowdate = new Date();
        int secKillStatus = 0;
        int remainSeconds = 0;
        if(nowdate.before(startDate)){
            remainSeconds = ((int)((startDate.getTime()-nowdate.getTime())/1000));
        }else if(nowdate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSeckillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        return RespBean.success(detailVo);
    }

//    @RequestMapping(value = "toDetail/{goodsId}" , produces = "text/html;charset=utf-8")
//    @ResponseBody
//    public String toDetail(@PathVariable Long goodsId , Model model , User user , HttpServletRequest request , HttpServletResponse response){
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //Redis中获取页面，如果不为空，直接返回页面
//        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
//        if (!StringUtils.isEmpty(html)){
//            return html;
//        }
//
//        model.addAttribute("user",user);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        //前端计时器参数设置
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date nowDate = new Date();
//        //秒杀状态
//        int seckillStatus = 0;
//        //秒杀倒计时
//        int remainSeconds = 0;
//        //秒杀还未开始
//        if (nowDate.before(startDate)){
//            //seckillStatus还是0 不处理seckillStatus
//            remainSeconds = (int)((startDate.getTime()-nowDate.getTime())/1000);
//
//        }else if (nowDate.after(endDate)){
//            //秒杀结束
//            seckillStatus = 2;
//            remainSeconds  = -1;
//        }else {
//            //秒杀进行中
//            seckillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("remainSeconds",remainSeconds);
//        model.addAttribute("seckillStatus",seckillStatus);
//        model.addAttribute("goods",goodsVo);
//
//        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context); // html , context
//        if (!StringUtils.isEmpty(html)){
//            valueOperations.set("goodsDetail" + goodsId,html,60,TimeUnit.SECONDS);
//        }
//        return html;
//        //  return "goodsDetail";
//    }
}
