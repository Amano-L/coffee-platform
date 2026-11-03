package com.coffee.order.controller;

import com.coffee.common.result.Result;
import com.coffee.common.util.SecurityUtil;
import com.coffee.order.dto.CreateOrderDTO;
import com.coffee.order.service.OrderService;
import com.coffee.order.vo.OrderSubmitVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单接口
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 创建订单
    @PostMapping
    public Result<OrderSubmitVO> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(orderService.createOrder(userId, dto));
    }
}