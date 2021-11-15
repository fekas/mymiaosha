package com.zhongbin.miaoshademo.controller;

import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IGoodsService;
import com.zhongbin.miaoshademo.service.IUserService;
import com.zhongbin.miaoshademo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;

    @RequestMapping("toList")
    public String toList(Model model, User user){
        //使用HandlerMethodArgumentResolver优化
//        if(StringUtils.isEmpty(ticket))
//            return "login";
//        //User user = ((User) session.getAttribute(ticket));
//        User user = userService.getUserByCookie(ticket, request, response);
//
        if(null == user){
            return "login";
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        return "goodsList";
    }

    @RequestMapping("toDetail/{goodsId}")
    public String toDetail(@PathVariable Long goodsId, Model model, User user){
        if(null == user){
            return "login";
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
        return "goodsDetail";
    }
}
