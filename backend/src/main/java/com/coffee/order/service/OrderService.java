package com.coffee.order.service;

import com.coffee.order.dto.CreateOrderDTO;
import com.coffee.order.vo.OrderSubmitVO;

/**
 * 订单业务接口
 */
public interface OrderService {
    OrderSubmitVO createOrder(Long userId, CreateOrderDTO dto);
    void cancelOrder(String orderNo);
}