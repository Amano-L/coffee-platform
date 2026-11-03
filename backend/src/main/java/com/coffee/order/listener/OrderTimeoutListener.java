package com.coffee.order.listener;

import com.coffee.common.constant.MqConstant;
import com.coffee.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 订单超时消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutListener {

    private final OrderService orderService;

    // 监听超时队列
    @RabbitListener(queues = MqConstant.ORDER_TIMEOUT_QUEUE)
    public void handleOrderTimeout(String orderNo) {
        log.info("收到订单超时消息: {}", orderNo);
        try {
            orderService.cancelOrder(orderNo);
        } catch (Exception e) {
            log.error("处理订单超时异常: orderNo={}", orderNo, e);
            // 如果抛出异常，RabbitMQ 会触发重试机制，或者进入死信队列（视配置而定）
            throw e; 
        }
    }
}