package com.zhongbin.miaoshademo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.vo.LoginVo;
import com.zhongbin.miaoshademo.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dabin
 * @since 2021-11-08
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
}
