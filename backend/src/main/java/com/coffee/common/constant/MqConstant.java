package com.coffee.common.constant;

/**
 * RabbitMQ 常量定义
 */
public class MqConstant {
    
    // ===== 订单超时交换机/队列 =====
    public static final String ORDER_EXCHANGE = "order.exchange";
    
    // 订单延迟队列（用于存放 30 分钟后过期的消息）
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay";
    
    // 订单超时死信队列（真正处理取消逻辑的队列）
    public static final String ORDER_TIMEOUT_QUEUE = "order.timeout.queue";
    public static final String ORDER_TIMEOUT_ROUTING_KEY = "order.timeout";
}