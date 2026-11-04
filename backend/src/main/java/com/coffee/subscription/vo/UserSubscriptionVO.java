package com.coffee.subscription.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户订阅出参
 */
@Data
public class UserSubscriptionVO {
    private Long id;
    /** 兼容《05-API设计文档》的创建订阅响应字段 */
    private Long subscriptionId;
    private String planName;
    private String period;
    private String status;
    private LocalDate nextDeliveryDate;
    private LocalDateTime createTime;
}