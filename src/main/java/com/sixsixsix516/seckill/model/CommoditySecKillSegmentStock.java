package com.sixsixsix516.seckill.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分段库存表
 *
 * @author SUN
 * @date 2022/5/4
 */
@Data
@TableName("commodity_seckill_segment_stock")
public class CommoditySecKillSegmentStock {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品id
     */
    private Long commodityId;

    /**
     * 段编号
     */
    private Byte segmentNo;

    /**
     * 总编号
     */
    private Integer totalStock;

    /**
     * 剩余库存
     */
    private Integer stock;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
