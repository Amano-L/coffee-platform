package com.coffee.subscription.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建订阅入参
 */
@Data
public class CreateSubscriptionDTO {
    @NotNull(message = "订阅计划 ID 不能为空")
    private Long planId;
}