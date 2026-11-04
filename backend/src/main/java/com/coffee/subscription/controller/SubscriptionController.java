package com.coffee.subscription.controller;

import com.coffee.common.result.Result;
import com.coffee.common.util.SecurityUtil;
import com.coffee.subscription.dto.CreateSubscriptionDTO;
import com.coffee.subscription.service.SubscriptionService;
import com.coffee.subscription.vo.SubscriptionPlanVO;
import com.coffee.subscription.vo.UserSubscriptionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订阅接口
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // 获取所有订阅计划 (白名单)
    @GetMapping("/plans")
    public Result<List<SubscriptionPlanVO>> getPlans() {
        return Result.success(subscriptionService.getPlans());
    }

    // 创建订阅
    @PostMapping
    public Result<UserSubscriptionVO> createSubscription(@Valid @RequestBody CreateSubscriptionDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(subscriptionService.createSubscription(userId, dto));
    }

    // 我的订阅
    @GetMapping("/my")
    public Result<List<UserSubscriptionVO>> getMySubscriptions() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(subscriptionService.getMySubscriptions(userId));
    }

    // 暂停订阅
    @PutMapping("/{id}/pause")
    public Result<UserSubscriptionVO> pause(@PathVariable Long id) {
        return Result.success(subscriptionService.pauseSubscription(SecurityUtil.getCurrentUserId(), id));
    }

    // 跳过本次配送
    @PutMapping("/{id}/skip")
    public Result<UserSubscriptionVO> skip(@PathVariable Long id) {
        return Result.success(subscriptionService.skipSubscription(SecurityUtil.getCurrentUserId(), id));
    }

    // 取消订阅
    @PutMapping("/{id}/cancel")
    public Result<UserSubscriptionVO> cancel(@PathVariable Long id) {
        return Result.success(subscriptionService.cancelSubscription(SecurityUtil.getCurrentUserId(), id));
    }
}