package com.zhongbin.miaoshademo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhongbin.miaoshademo.pojo.MiaoshaOrder;
import com.zhongbin.miaoshademo.pojo.Order;
import com.zhongbin.miaoshademo.pojo.User;
import com.zhongbin.miaoshademo.service.IGoodsService;
import com.zhongbin.miaoshademo.service.IMiaoshaOrderService;
import com.zhongbin.miaoshademo.service.IOrderService;
import com.zhongbin.miaoshademo.vo.GoodsVo;
import com.zhongbin.miaoshademo.vo.RespBean;
import com.zhongbin.miaoshademo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("miaosha")
public class MiaoshaController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IMiaoshaOrderService miaoshaOrderService;
    @Autowired
    private IOrderService orderService;

    /**
     * mac优化前QPS：109
     * centOS优化前：853.3
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
        MiaoshaOrder miaoshaOrder = miaoshaOrderService.getOne(new QueryWrapper<MiaoshaOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
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
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        if(goodsVo.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.ENPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.ENPTY_STOCK);
        }
        MiaoshaOrder miaoshaOrder = miaoshaOrderService.getOne(new QueryWrapper<MiaoshaOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(miaoshaOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        Order order = orderService.miaosha(user, goodsVo);
        return RespBean.success(order);
    }
}
