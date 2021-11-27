package com.zhongbin.miaoshademo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhongbin.miaoshademo.pojo.Order;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.vo.GoodsVo;
import com.zhongbin.miaoshademo.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dabin
 * @since 2021-11-13
 */
public interface IOrderService extends IService<Order> {

    Order miaosha(User user, GoodsVo goodsVo);

    OrderDetailVo detail(Long userId, Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
