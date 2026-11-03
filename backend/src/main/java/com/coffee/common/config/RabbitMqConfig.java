package com.coffee.common.config;

import com.coffee.common.constant.MqConstant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 配置，主要声明延迟队列与死信队列
 */
@Configuration
public class RabbitMqConfig {

    // 1. 订单业务交换机
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(MqConstant.ORDER_EXCHANGE, true, false);
    }

    // 2. 订单延迟队列（消息在这里存活 30 分钟后变成死信）
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        // 设置死信交换机
        args.put("x-dead-letter-exchange", MqConstant.ORDER_EXCHANGE);
        // 设置死信路由键（指向超时队列）
        args.put("x-dead-letter-routing-key", MqConstant.ORDER_TIMEOUT_ROUTING_KEY);
        // 设置消息 TTL（30分钟 = 1800000 毫秒）
        args.put("x-message-ttl", 1800000);
        // 临时测试，改为 10 秒
        // args.put("x-message-ttl", 10000);
        return QueueBuilder.durable(MqConstant.ORDER_DELAY_QUEUE).withArguments(args).build();
    }

    // 延迟队列绑定到交换机
    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderExchange()).with(MqConstant.ORDER_DELAY_ROUTING_KEY);
    }

    // 3. 订单超时处理队列（真正的消费者监听这里）
    @Bean
    public Queue orderTimeoutQueue() {
        return new Queue(MqConstant.ORDER_TIMEOUT_QUEUE, true);
    }

    // 超时队列绑定到交换机
    @Bean
    public Binding orderTimeoutBinding() {
        return BindingBuilder.bind(orderTimeoutQueue()).to(orderExchange()).with(MqConstant.ORDER_TIMEOUT_ROUTING_KEY);
    }
}