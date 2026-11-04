package com.coffee.order.service;

import com.coffee.order.dto.CreateOrderDTO;
import com.coffee.order.vo.OrderDetailVO;
import com.coffee.order.vo.OrderListVO;
import com.coffee.order.vo.OrderSubmitVO;

/**
 * 订单业务接口
 */
public interface OrderService {

    // 创建订单
    OrderSubmitVO createOrder(Long userId, CreateOrderDTO dto);
    // 订单超时自动取消
    void cancelOrder(String orderNo);
    // 获取订单列表
    com.baomidou.mybatisplus.core.metadata.IPage<OrderListVO> getOrderList(Long userId, Integer page, Integer size, String status);

    // 获取订单详情
    OrderDetailVO getOrderDetail(Long userId, String orderNo);

    // 手动取消订单
    void manualCancelOrder(Long userId, String orderNo);

    // 支付成功后更新订单状态
    void updateOrderToPaid(String orderNo);
}