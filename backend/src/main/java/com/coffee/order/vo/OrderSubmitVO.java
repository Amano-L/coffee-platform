package com.coffee.order.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建订单出参
 */
@Data
public class OrderSubmitVO {
    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String status;
}