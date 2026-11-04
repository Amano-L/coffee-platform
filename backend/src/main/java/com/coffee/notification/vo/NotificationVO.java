package com.coffee.notification.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站内通知出参。
 */
@Data
public class NotificationVO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Integer isRead;
    private LocalDateTime createTime;
}
