package com.coffee.subscription.service;

import com.coffee.subscription.dto.CreateSubscriptionDTO;
import com.coffee.subscription.vo.SubscriptionPlanVO;
import com.coffee.subscription.vo.UserSubscriptionVO;

import java.util.List;

/**
 * 订阅业务接口
 */
public interface SubscriptionService {
    // 获取所有可用计划
    List<SubscriptionPlanVO> getPlans();

    // 创建订阅
    UserSubscriptionVO createSubscription(Long userId, CreateSubscriptionDTO dto);

    // 我的订阅
    List<UserSubscriptionVO> getMySubscriptions(Long userId);

    // 暂停订阅
    UserSubscriptionVO pauseSubscription(Long userId, Long id);

    // 跳过本次配送
    UserSubscriptionVO skipSubscription(Long userId, Long id);

    // 取消订阅
    UserSubscriptionVO cancelSubscription(Long userId, Long id);

    // 供定时任务调用：处理到期的订阅
    void processExpiredSubscriptions();
}