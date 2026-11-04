package com.coffee.admin.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 后台-订单列表出参
 */
@Data
public class AdminOrderVO {
    private String orderNo;
    private Long userId;
    private BigDecimal payAmount;
    private String status;
    private LocalDateTime createTime;
}