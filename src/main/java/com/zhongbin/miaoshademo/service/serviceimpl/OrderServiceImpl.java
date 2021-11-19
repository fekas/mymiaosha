package com.zhongbin.miaoshademo.service.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongbin.miaoshademo.mapper.OrderMapper;
import com.zhongbin.miaoshademo.pojo.MiaoshaGoods;
import com.zhongbin.miaoshademo.pojo.MiaoshaOrder;
import com.zhongbin.miaoshademo.pojo.Order;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IMiaoshaGoodsService;
import com.zhongbin.miaoshademo.service.IMiaoshaOrderService;
import com.zhongbin.miaoshademo.service.IOrderService;
import com.zhongbin.miaoshademo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dabin
 * @since 2021-11-13
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private IMiaoshaGoodsService miaoshaGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private IMiaoshaOrderService miaoshaOrderService;

    @Override
    public Order miaosha(User user, GoodsVo goodsVo) {
        MiaoshaGoods miaoshaGoods = miaoshaGoodsService.getOne(new QueryWrapper<MiaoshaGoods>().eq("goods_id", goodsVo.getId()));
        miaoshaGoods.setStockCount(miaoshaGoods.getStockCount() - 1);
        miaoshaGoodsService.updateById(miaoshaGoods);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddId(0l);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goodsVo.getMiaoshaPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());

        orderMapper.insert(order);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setOrderId(order.getId());
        miaoshaOrder.setGoodsId(goodsVo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrderService.save(miaoshaOrder);

        return order;
    }
}