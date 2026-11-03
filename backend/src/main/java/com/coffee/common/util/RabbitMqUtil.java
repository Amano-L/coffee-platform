package com.coffee.common.util;

import com.coffee.common.constant.MqConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 发送工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqUtil {

    private final RabbitTemplate rabbitTemplate;

    // 发送订单超时延迟消息
    public void sendOrderTimeoutMessage(String orderNo) {
        log.info("发送订单超时延迟消息: orderNo={}", orderNo);
        rabbitTemplate.convertAndSend(
                MqConstant.ORDER_EXCHANGE,
                MqConstant.ORDER_DELAY_ROUTING_KEY,
                orderNo
        );
    }
}