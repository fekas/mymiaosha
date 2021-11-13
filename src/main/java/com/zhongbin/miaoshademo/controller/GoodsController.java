package com.zhongbin.miaoshademo.controller;

import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;

    @RequestMapping("toList")
    public String toList(Model model, User user){
//        if(StringUtils.isEmpty(ticket))
//            return "login";
//        //User user = ((User) session.getAttribute(ticket));
//        User user = userService.getUserByCookie(ticket, request, response);
//
        if(null == user){
            return "login";
        }
        model.addAttribute("user", user);
        return "goodsList";
    }

    @RequestMapping("toDetail")
    public String toDetail(Model model, User user){

        model.addAttribute("user", user);
        return "goodsList";
    }
}
