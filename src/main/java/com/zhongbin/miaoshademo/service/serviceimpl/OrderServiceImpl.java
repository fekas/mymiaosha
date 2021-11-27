package com.zhongbin.miaoshademo.service.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongbin.miaoshademo.exception.GlobalException;
import com.zhongbin.miaoshademo.mapper.OrderMapper;
import com.zhongbin.miaoshademo.pojo.MiaoshaGoods;
import com.zhongbin.miaoshademo.pojo.MiaoshaOrder;
import com.zhongbin.miaoshademo.pojo.Order;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IGoodsService;
import com.zhongbin.miaoshademo.service.IMiaoshaGoodsService;
import com.zhongbin.miaoshademo.service.IMiaoshaOrderService;
import com.zhongbin.miaoshademo.service.IOrderService;
import com.zhongbin.miaoshademo.utils.MD5Util;
import com.zhongbin.miaoshademo.utils.UUIDUtil;
import com.zhongbin.miaoshademo.vo.GoodsVo;
import com.zhongbin.miaoshademo.vo.OrderDetailVo;
import com.zhongbin.miaoshademo.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dabin
 * @since 2021-11-13
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private IMiaoshaGoodsService miaoshaGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private IMiaoshaOrderService miaoshaOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public Order miaosha(User user, GoodsVo goodsVo) {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        MiaoshaGoods miaoshaGoods = miaoshaGoodsService.getOne(new QueryWrapper<MiaoshaGoods>().eq("goods_id", goodsVo.getId()));
        miaoshaGoods.setStockCount(miaoshaGoods.getStockCount() - 1);
       // miaoshaGoodsService.updateById(miaoshaGoods);
 //       boolean miaoshaResult = miaoshaGoodsService.update(new UpdateWrapper<MiaoshaGoods>()
   //     .set("stock_count", miaoshaGoods.getStockCount()).eq("id", miaoshaGoods.getId()).gt("stock_count", 0));

        boolean result = miaoshaGoodsService.update(
                new UpdateWrapper<MiaoshaGoods>()
                        .setSql("stock_count = stock_count - 1")
                        .eq("goods_id", goodsVo.getId())
                        .gt("stock_count", 0)
        );
        if(miaoshaGoods.getStockCount() < 1){
            //判断是否有库存
            valueOperations.set("isStockEmpty:" + goodsVo.getId(), "0");
            return null;
        }
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

        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goodsVo.getId(), miaoshaOrder);
        return order;
    }

    @Override
    public OrderDetailVo detail(Long userId, Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        if(!order.getUserId().equals(userId)){
            throw new GlobalException(RespBeanEnum.ERROR);
        }
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());

        OrderDetailVo detail = new OrderDetailVo(order, goodsVo);

        return detail;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");

        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("miaoshaPath:" + user.getId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if(user == null || goodsId < 0 || StringUtils.isEmpty(path))return false;
        String redisPath = ((String) redisTemplate.opsForValue().get("miaoshaPath:" + user.getId() + ":" + goodsId));
        log.info(redisPath + "==" + path);
        return path.equals(redisPath);
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(user == null|| goodsId < 0 || StringUtils.isEmpty(captcha)){
            return false;
        }
        String redisCaptcha = ((String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId));
        return captcha.equals(redisCaptcha);
    }
}
