package com.zhongbin.miaoshademo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhongbin.miaoshademo.pojo.Order;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.vo.GoodsVo;

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
}
