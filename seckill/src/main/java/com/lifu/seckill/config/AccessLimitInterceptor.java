package com.lifu.seckill.config;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifu.seckill.pojo.User;
import com.lifu.seckill.service.UserService;
import com.lifu.seckill.utils.CookieUtils;
import com.lifu.seckill.vo.RespBean;
import com.lifu.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Configuration
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            User user = getUser(request,response);
            UserContext.setUser(user);

            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class); //获取方法上的注解
            if(accessLimit == null){
                return true;
            }

            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String uri = request.getRequestURI();

            if(needLogin) {
                if (user == null) {
                    render(response, RespBeanEnum.SESSION_ERROR); //返回json,因为这不能返回RespBean类
                    return false;
                }
                uri += ":" + user.getId();
            }

                ValueOperations valueOperations = redisTemplate.opsForValue();
                Integer count = (Integer)valueOperations.get(uri + ":" + user.getId());

                if(count == null){
                    valueOperations.set(uri + ":" + user.getId() , 1 , second , TimeUnit.SECONDS);
                }else if(count <= maxCount){
                    valueOperations.increment( uri + ":" + user.getId());
                }else{
                    render(response,RespBeanEnum.ACCESS_LIMIT);
                    return false;
                }

        }
        return true;
    }

    //构建返回对象
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }

    public User getUser(HttpServletRequest request , HttpServletResponse response){
        String ticket = CookieUtils.getCookieValue(request, "userTicket");

        if (StringUtils.isEmpty(ticket)){
            return null;
        }

        // 返回user 对象
        return userService.getUserByCookie(ticket,request,response);
    }
}
