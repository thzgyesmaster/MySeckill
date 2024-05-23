package com.lifu.seckill.controller;

import com.lifu.seckill.pojo.User;
import com.lifu.seckill.service.GoodsService;
import com.lifu.seckill.service.UserService;
import com.lifu.seckill.vo.GoodsVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

import java.util.Date;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/toList")
    public String toList(User user , Model model){


        model.addAttribute("user" , user);
        model.addAttribute("goodsList" , goodsService.findGoodsVo() );

        return "goodsList";
    }

    /**
     * 跳转商品详情
     * @param goodsId
     * @return
     */
    @RequestMapping("toDetail/{goodsId}")
    public String toDetail(@PathVariable Long goodsId , Model model , User user){
        model.addAttribute("user" , user);

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();

        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;

        if(nowDate.before(startDate)){
            remainSeconds = (int) ( (startDate.getTime() - nowDate.getTime()) / 1000 );
        }else if(nowDate.after(endDate)){
            //秒杀已结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else{
            //秒杀中
            secKillStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("goods" , goodsVo);
        model.addAttribute("secKillStatus" , secKillStatus);
        model.addAttribute("remainSeconds" , remainSeconds);

        return "goodsDetail";
    }
}
