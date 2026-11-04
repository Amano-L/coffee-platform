package com.coffee.payment.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 发起支付出参
 */
@Data
public class PaymentVO {
    private String payNo;
    private String orderNo;
    private BigDecimal amount;
    private String status;
}