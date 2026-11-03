package com.coffee.catalog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 咖啡 SKU 实体
 */
@Data
@TableName("coffee_sku")
public class CoffeeSku {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String weight;
    private String grindType;
    private BigDecimal price;
    private Integer stock;
    @Version
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}