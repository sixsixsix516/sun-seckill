package com.sixsixsix516.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sixsixsix516.seckill.model.Commodity;
import org.apache.ibatis.annotations.Select;

/**
 * @author SUN
 * @date 2022/5/3
 */
public interface CommodityMapper extends BaseMapper<Commodity> {


    @Select("UPDATE commodity SET stock = stock - 1 WHERE id = #{commodityId}")
    void decreaseStockById(Long commodityId);
}
