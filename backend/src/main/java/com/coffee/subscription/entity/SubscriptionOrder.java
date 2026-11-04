package com.coffee.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订阅订单关联实体
 */
@Data
@TableName("subscription_order")
public class SubscriptionOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userSubscriptionId;
    private Long orderId;
    private String orderNo;
    private LocalDate deliveryDate;
    private String status;
    private LocalDateTime createTime;
}