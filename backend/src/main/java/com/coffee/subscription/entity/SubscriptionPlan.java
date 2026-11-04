package com.coffee.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订阅计划实体
 */
@Data
@TableName("subscription_plan")
public class SubscriptionPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long skuId;
    private String period;
    private BigDecimal price;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}