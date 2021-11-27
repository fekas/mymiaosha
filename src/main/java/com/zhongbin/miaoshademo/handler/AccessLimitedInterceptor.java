package com.zhongbin.miaoshademo.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongbin.miaoshademo.annotations.AccessLimited;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IUserService;
import com.zhongbin.miaoshademo.utils.CookieUtil;
import com.zhongbin.miaoshademo.vo.RespBean;
import com.zhongbin.miaoshademo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class AccessLimitedInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod handler1 = (HandlerMethod) handler;
            AccessLimited accessLimited = handler1.getMethodAnnotation(AccessLimited.class);
            if(accessLimited == null){
                return true;
            }
            int second = accessLimited.second();
            int maxCnt = accessLimited.maxCnt();
            boolean needLogin = accessLimited.needLogin();

            String uri = request.getRequestURI();
            if(needLogin){
                if(user == null){
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                String key = uri + ":" + user.getId();
                ValueOperations valueOperations = redisTemplate.opsForValue();
                Integer redisCnt = ((Integer) valueOperations.get(key));
                if(redisCnt == null){
                    valueOperations.set(key, 1, second, TimeUnit.SECONDS);
                }else if(redisCnt < maxCnt){
                    valueOperations.increment(key);
                }else{
                    render(response, RespBeanEnum.ACCESS_LIMITED_REACHED);
                    return false;
                }
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isEmpty(ticket))
            return null;
        return userService.getUserByCookie(ticket, request, response);
    }
}
