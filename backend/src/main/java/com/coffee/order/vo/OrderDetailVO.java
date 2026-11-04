package com.coffee.order.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情出参
 */
@Data
public class OrderDetailVO {
    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;
}