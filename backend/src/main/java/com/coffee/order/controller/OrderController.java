package com.coffee.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.common.result.Result;
import com.coffee.common.util.SecurityUtil;
import com.coffee.order.dto.CreateOrderDTO;
import com.coffee.order.service.OrderService;
import com.coffee.order.vo.OrderDetailVO;
import com.coffee.order.vo.OrderListVO;
import com.coffee.order.vo.OrderSubmitVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    // 订单列表
    @GetMapping
    public Result<IPage<OrderListVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(orderService.getOrderList(userId, page, size, status));
    }

    // 订单详情
    @GetMapping("/{orderNo}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable String orderNo) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(orderService.getOrderDetail(userId, orderNo));
    }

    // 手动取消订单
    @PostMapping("/{orderNo}/cancel")
    public Result<Void> cancelOrder(@PathVariable String orderNo) {
        Long userId = SecurityUtil.getCurrentUserId();
        orderService.manualCancelOrder(userId, orderNo);
        return Result.success();
    }
}