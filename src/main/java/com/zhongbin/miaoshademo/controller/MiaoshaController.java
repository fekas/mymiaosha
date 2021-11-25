package com.zhongbin.miaoshademo.controller;

import com.alibaba.fastjson.JSON;
import com.zhongbin.miaoshademo.pojo.MiaoshaMessage;
import com.zhongbin.miaoshademo.pojo.MiaoshaOrder;
import com.zhongbin.miaoshademo.pojo.Order;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.rabbitmq.MQSender;
import com.zhongbin.miaoshademo.service.IGoodsService;
import com.zhongbin.miaoshademo.service.IMiaoshaOrderService;
import com.zhongbin.miaoshademo.service.IOrderService;
import com.zhongbin.miaoshademo.vo.GoodsVo;
import com.zhongbin.miaoshademo.vo.RespBean;
import com.zhongbin.miaoshademo.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IMiaoshaOrderService miaoshaOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> redisScript;

    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    /**
     * mac优化前QPS：109    优化后575
     * centOS优化前：853.3
     * centOS优化后: 988   1197
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("domiaosha2")
    public String doMiaosha2(Model model,User user, Long goodsId){
        if(user == null)return "login";
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        if(goodsVo.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.ENPTY_STOCK.getMessage());
            return "miaoshaFail";
        }

        //在秒杀订单表建立user_id和goods_id的唯一索引也可以防止重复购买
        //MiaoshaOrder miaoshaOrder = miaoshaOrderService.getOne(new QueryWrapper<MiaoshaOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        MiaoshaOrder miaoshaOrder = ((MiaoshaOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId()));
        if(miaoshaOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "miaoshaFail";
        }
        Order order = orderService.miaosha(user, goodsVo);
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);
        return "orderDetail";
    }

    @PostMapping("domiaosha")
    @ResponseBody
    public RespBean doMiaosha(Model model, User user, Long goodsId){
        if(user == null)return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        if(goodsVo.getStockCount() < 1){
//            model.addAttribute("errmsg", RespBeanEnum.ENPTY_STOCK.getMessage());
//            return RespBean.error(RespBeanEnum.ENPTY_STOCK);
//        }
//        MiaoshaOrder miaoshaOrder = miaoshaOrderService.getOne(new QueryWrapper<MiaoshaOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
//        if(miaoshaOrder != null){
//            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
//            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
//        }
//        Order order = orderService.miaosha(user, goodsVo);
//        return RespBean.success(order);

        //通过redis预减库存来优化上面的代码

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断是否重复抢购
        //MiaoshaOrder miaoshaOrder = miaoshaOrderService.getOne(new QueryWrapper<MiaoshaOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        MiaoshaOrder miaoshaOrder = ((MiaoshaOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId));
        if(miaoshaOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //通过内存标记减少redis的访问
        if (emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.ENPTY_STOCK);
        }
        //预减库存
        Long stock = valueOperations.decrement("miaoshaGoods:" + goodsId);
//        Long stock = ((Long) redisTemplate.execute(redisScript, Collections.singletonList("miaoshaGoods:" + goodsId), Collections.EMPTY_LIST));
        if(stock < 0){
            emptyStockMap.put(goodsId, true);
            valueOperations.increment("miaoshaGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.ENPTY_STOCK);
        }
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage(user, goodsId);
        mqSender.sendMiaoshaMessage(JSON.toJSONString(miaoshaMessage));
//        Order order = orderService.miaosha(user, goodsVo);
        return RespBean.success(0);
    }

    @GetMapping("/result")
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if(user == null)return RespBean.error(RespBeanEnum.SESSION_ERROR);
        Long orderId = miaoshaOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 系统初始化时把商品加入到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(goodsVoList)){
            return;
        }
        goodsVoList.forEach(goodsVo -> {
                    redisTemplate.opsForValue().set("miaoshaGoods:" + goodsVo.getId(), goodsVo.getStockCount());
                    emptyStockMap.put(goodsVo.getId(), false);
                }
            );
    }
}
