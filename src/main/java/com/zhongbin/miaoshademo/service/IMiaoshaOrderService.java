package com.zhongbin.miaoshademo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhongbin.miaoshademo.pojo.MiaoshaOrder;
import com.zhongbin.miaoshademo.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dabin
 * @since 2021-11-13
 */
public interface IMiaoshaOrderService extends IService<MiaoshaOrder> {

    Long getResult(User user, Long goodsId);
}
