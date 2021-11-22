package com.zhongbin.miaoshademo.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.zhongbin.miaoshademo.pojo.MiaoshaMessage;
import com.zhongbin.miaoshademo.pojo.MiaoshaOrder;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IGoodsService;
import com.zhongbin.miaoshademo.service.IOrderService;
import com.zhongbin.miaoshademo.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "miaoshaQueue")
    public void receiver(String message){
        log.info("接收消息：" + message);
        MiaoshaMessage miaoshaMessage = JSON.parseObject(message, MiaoshaMessage.class);
        Long goodsId = miaoshaMessage.getGoodsId();
        User user = miaoshaMessage.getUser();

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() < 1){
            return;
        }
        MiaoshaOrder miaoshaOrder = ((MiaoshaOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId));
        if(miaoshaOrder != null){
            return;
        }
        orderService.miaosha(user, goodsVo);
    }
//    @RabbitListener(queues = "queue_fanout01")
//    public void receiver01(Object msg){
//        log.info("接收消息：" + msg);
//    }
//    @RabbitListener(queues = "queue_fanout02")
//    public void receiver02(Object msg){
//        log.info("接收消息：" + msg);
//    }
}
