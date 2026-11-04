package com.coffee.subscription.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订阅计划出参
 */
@Data
public class SubscriptionPlanVO {
    private Long id;
    private String name;
    private String period;
    private BigDecimal price;
    private String description;
}