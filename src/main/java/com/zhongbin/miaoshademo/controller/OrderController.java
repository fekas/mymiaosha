package com.zhongbin.miaoshademo.controller;


import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IOrderService;
import com.zhongbin.miaoshademo.vo.OrderDetailVo;
import com.zhongbin.miaoshademo.vo.RespBean;
import com.zhongbin.miaoshademo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dabin
 * @since 2021-11-13
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @RequestMapping("/detail")
    public RespBean detail(User user, Long orderId){
        if(user == null)
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        OrderDetailVo orderDetailVo = orderService.detail(user.getId() ,orderId);
        return RespBean.success(orderDetailVo);
    }
}
