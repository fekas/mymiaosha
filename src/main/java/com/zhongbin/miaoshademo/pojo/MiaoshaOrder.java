package com.zhongbin.miaoshademo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author dabin
 * @since 2021-11-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_miaosha_order")
public class MiaoshaOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 秒杀order编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * goods id
     */
    private Long goodsId;


}
