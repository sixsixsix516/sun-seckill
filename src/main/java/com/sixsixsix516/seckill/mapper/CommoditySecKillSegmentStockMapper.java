package com.sixsixsix516.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sixsixsix516.seckill.model.CommoditySecKillSegmentStock;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author SUN
 * @date 2022/5/4
 */
public interface CommoditySecKillSegmentStockMapper extends BaseMapper<CommoditySecKillSegmentStock> {


    /**
     * 批量添加分段库存信息
     *
     * @param commoditySecKillSegmentStocks 分段库存信息
     */
    void batchInsert(List<CommoditySecKillSegmentStock> commoditySecKillSegmentStocks);

    /**
     * 获取指定商品库存
     *
     * @param commodityId 商品id
     * @return 总库存
     */
    @Select("SELECT SUM(stock) FROM commodity_seckill_segment_stock WHERE commodity_id = #{commodityId}")
    int getStockByCommodityId(long commodityId);

    /**
     * 分段减少库存
     */
    @Select("UPDATE commodity_seckill_segment_stock SET stock = stock - 1 WHERE commodity_id = #{commodityId} AND segment_no = #{segmentNo}")
    void decraseStock(long commodityId, Integer segmentNo);

}
