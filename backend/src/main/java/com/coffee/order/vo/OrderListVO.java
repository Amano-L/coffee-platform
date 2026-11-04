package com.coffee.order.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表出参
 */
@Data
public class OrderListVO {
    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String status;
    private LocalDateTime createTime;
}