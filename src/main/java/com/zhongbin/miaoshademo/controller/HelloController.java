package com.zhongbin.miaoshademo.controller;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/hello")
public class HelloController {

//    @RequestMapping(value = "/name")
//    public String sayHello(@RequestParam(value = "name", required = false) String name){
//        if(StrUtil.isBlank(name)){
//            return "Who is your lover?";
//        }
//        return StrUtil.format("Hello, my dear {}.", name);
//    }

    /**
     * 测试页面跳转
     * @param model
     * @return
     */
    @RequestMapping("/all")
    public String helloAll(Model model){
        model.addAttribute("name", "Jennifer");
        return "hello";
    }
}
