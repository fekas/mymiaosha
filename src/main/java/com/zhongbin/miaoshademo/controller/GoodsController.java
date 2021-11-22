package com.zhongbin.miaoshademo.controller;

import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IGoodsService;
import com.zhongbin.miaoshademo.service.IUserService;
import com.zhongbin.miaoshademo.vo.DetailVo;
import com.zhongbin.miaoshademo.vo.GoodsVo;
import com.zhongbin.miaoshademo.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 压测
     * 两用户.mac优化前QPS:103.5
     * 两用户，centOS优化前QPS435.3
     * 页面缓存centOS优化后QPS813
     *
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response){
        //使用HandlerMethodArgumentResolver优化
//        if(StringUtils.isEmpty(ticket))
//            return "login";
//        //User user = ((User) session.getAttribute(ticket));
//        User user = userService.getUserByCookie(ticket, request, response);
//

        //尝试在redis中获取页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = ((String) valueOperations.get("goodsList"));
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        //手动渲染，存入redis，返回
        WebContext webContext = new WebContext(request, response,request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        //return "goodsList";
        return html;
    }

    @RequestMapping(value = "toDetail2/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(@PathVariable Long goodsId, Model model, User user, HttpServletRequest request, HttpServletResponse response){

        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = ((String) valueOperations.get("goodsDetail:" + goodsId));

        if (!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int miaoshaStatus = 0;
        Long remainSeconds = 0L;
        if(nowDate.before(startDate)){
            remainSeconds = (Long)((startDate.getTime() - nowDate.getTime()) / 1000);
        }
        else if(nowDate.after(endDate)){
            miaoshaStatus = 2;
            remainSeconds = -1L;
        }
        else miaoshaStatus = 1;

        model.addAttribute("miaoshaStatus" ,miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("goods", goodsVo);

        WebContext webContext = new WebContext(request, response,request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);

        if (!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail" + goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    @RequestMapping(value = "toDetail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(@PathVariable Long goodsId, Model model, User user){

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if(nowDate.before(startDate)){
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime()) / 1000);
        }
        else if(nowDate.after(endDate)){
            miaoshaStatus = 2;
            remainSeconds = -1;
        }
        else miaoshaStatus = 1;

        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setMiaoshaStatus(miaoshaStatus);
        detailVo.setRemainSeconds(remainSeconds);

        log.info(detailVo.toString());
        return RespBean.success(detailVo);
    }
}
