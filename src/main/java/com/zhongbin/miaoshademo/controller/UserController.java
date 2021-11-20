package com.zhongbin.miaoshademo.controller;


import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dabin
 * @since 2021-11-08
 */
@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * 用户信息
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

}
