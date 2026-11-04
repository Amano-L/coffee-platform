package com.coffee.admin.dto;

import lombok.Data;

/**
 * 后台订阅列表查询入参。
 */
@Data
public class AdminSubscriptionQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    /** ACTIVE / PAUSED / CANCELLED，不传则查询全部 */
    private String status;
}
