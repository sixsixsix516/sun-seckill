package com.sixsixsix516.seckill.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品
 *
 * @author SUN
 * @date 2022/5/3
 */
@Data
public class Commodity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品信息
     */
    private String name;

    /**
     * 库存
     */
    private Integer stock;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
