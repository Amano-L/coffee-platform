package com.coffee.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体
 */
@Data
@TableName("coffee_order_item")
public class CoffeeOrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long skuId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime createTime;
}