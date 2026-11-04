package com.coffee.order.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单项出参
 */
@Data
public class OrderItemVO {
    private Long skuId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}