package com.zhongbin.miaoshademo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
@TableName("t_miaosha_goods")
public class MiaoshaGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 秒杀商品编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品编号
     */
    private Long goodsId;

    /**
     * 秒杀价格
     */
    private BigDecimal miaoshaPrice;

    /**
     * 秒杀数量
     */
    private Integer stockCount;

    /**
     * 秒杀开始时间内
     */
    private Date startDate;

    /**
     * 秒杀结束时间
     */
    private Date endDate;


}
