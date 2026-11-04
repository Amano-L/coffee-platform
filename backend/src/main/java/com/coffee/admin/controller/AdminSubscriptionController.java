package com.coffee.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.admin.dto.AdminSubscriptionQueryDTO;
import com.coffee.admin.service.AdminSubscriptionService;
import com.coffee.admin.vo.AdminSubscriptionVO;
import com.coffee.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台订阅管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/subscriptions")
@RequiredArgsConstructor
public class AdminSubscriptionController {

    private final AdminSubscriptionService adminSubscriptionService;

    // 订阅列表
    @GetMapping
    public Result<IPage<AdminSubscriptionVO>> getSubscriptionPage(AdminSubscriptionQueryDTO query) {
        return Result.success(adminSubscriptionService.getSubscriptionPage(query));
    }

    // 暂停订阅
    @PutMapping("/{id}/pause")
    public Result<Map<String, Object>> pause(@PathVariable Long id) {
        adminSubscriptionService.pauseSubscription(id);
        return Result.success(Map.of("id", id, "status", "PAUSED"));
    }

    // 取消订阅
    @PutMapping("/{id}/cancel")
    public Result<Map<String, Object>> cancel(@PathVariable Long id) {
        adminSubscriptionService.cancelSubscription(id);
        return Result.success(Map.of("id", id, "status", "CANCELLED"));
    }
}
