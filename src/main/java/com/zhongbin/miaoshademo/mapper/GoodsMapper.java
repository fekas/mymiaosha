package com.zhongbin.miaoshademo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhongbin.miaoshademo.pojo.Goods;
import com.zhongbin.miaoshademo.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dabin
 * @since 2021-11-13
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
