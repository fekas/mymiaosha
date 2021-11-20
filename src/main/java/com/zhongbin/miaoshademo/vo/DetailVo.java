package com.zhongbin.miaoshademo.vo;

import com.zhongbin.miaoshademo.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {
    private User user;
    private GoodsVo goodsVo;
    private int miaoshaStatus;
    private int remainSeconds;
}
