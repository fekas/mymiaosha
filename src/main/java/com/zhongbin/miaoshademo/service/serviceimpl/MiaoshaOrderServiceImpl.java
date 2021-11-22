package com.zhongbin.miaoshademo.service.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongbin.miaoshademo.mapper.MiaoshaOrderMapper;
import com.zhongbin.miaoshademo.pojo.MiaoshaOrder;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IMiaoshaOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dabin
 * @since 2021-11-13
 */
@Service
public class MiaoshaOrderServiceImpl extends ServiceImpl<MiaoshaOrderMapper, MiaoshaOrder> implements IMiaoshaOrderService {

    @Autowired
    private MiaoshaOrderMapper miaoshaOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Long getResult(User user, Long goodsId) {
        MiaoshaOrder miaoshaOrder = miaoshaOrderMapper.selectOne(new QueryWrapper<MiaoshaOrder>()
                .eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(null != miaoshaOrder){
            return miaoshaOrder.getOrderId();
        }else if(redisTemplate.hasKey("isStockEmpty:" + goodsId)){
            return -1L;
        }else {
            return 0L;
        }
    }
}
