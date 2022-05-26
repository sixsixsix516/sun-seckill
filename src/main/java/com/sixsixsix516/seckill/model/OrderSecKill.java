package com.sixsixsix516.seckill.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author SUN
 * @date 2022/5/6
 */
@Data
@TableName("order_seckill")
public class OrderSecKill {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long commodityId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
