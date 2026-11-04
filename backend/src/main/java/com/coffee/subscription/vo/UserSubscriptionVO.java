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
    private String planName;
    private String period;
    private String status;
    private LocalDate nextDeliveryDate;
    private LocalDateTime createTime;
}