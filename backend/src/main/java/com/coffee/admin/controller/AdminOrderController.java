package com.coffee.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.admin.service.AdminOrderService;
import com.coffee.admin.vo.AdminOrderVO;
import com.coffee.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台订单管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    // 订单列表
    @GetMapping
    public Result<IPage<AdminOrderVO>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminOrderService.getOrderPage(page, size));
    }

    // 订单发货
    @PutMapping("/{orderNo}/ship")
    public Result<Map<String, Object>> shipOrder(@PathVariable String orderNo) {
        adminOrderService.shipOrder(orderNo);
        return Result.success(Map.of("orderNo", orderNo, "status", "已发货"));
    }
}