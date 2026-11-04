package com.coffee.admin.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 后台-数据看板出参
 */
@Data
public class DashboardVO {
    // 总销售额
    private BigDecimal totalSales;
    // 订单总数
    private Long orderCount;
    // 订阅总数
    private Long subscriptionCount;
    // 活跃订阅数
    private Long activeSubscriptionCount;
}