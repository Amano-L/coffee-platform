package com.coffee.admin.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 后台订阅列表出参。
 */
@Data
public class AdminSubscriptionVO {
    private Long id;
    private Long userId;
    private String username;
    private String planName;
    private String period;
    private String status;
    private LocalDate nextDeliveryDate;
    private LocalDateTime createTime;
}
