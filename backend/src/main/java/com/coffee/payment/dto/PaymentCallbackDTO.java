package com.coffee.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 支付回调入参
 */
@Data
public class PaymentCallbackDTO {
    @NotBlank(message = "支付单号不能为空")
    private String payNo;

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @NotBlank(message = "状态不能为空")
    private String status;

    @NotBlank(message = "签名不能为空")
    private String sign;
}