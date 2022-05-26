package com.sixsixsix516.seckill.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品秒杀
 *
 * @author SUN
 * @date 2022/5/3
 */
@Data
@TableName("commodity_seckill")
public class CommoditySecKill {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品id
     */
    private Long commodityId;

    private String exposeKey;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
