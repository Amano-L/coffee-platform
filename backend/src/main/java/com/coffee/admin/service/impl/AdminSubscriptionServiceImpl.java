package com.coffee.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coffee.admin.dto.AdminSubscriptionQueryDTO;
import com.coffee.admin.service.AdminSubscriptionService;
import com.coffee.admin.vo.AdminSubscriptionVO;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.notification.service.NotificationService;
import com.coffee.subscription.entity.UserSubscription;
import com.coffee.subscription.mapper.UserSubscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 后台订阅管理实现。
 */
@Service
@RequiredArgsConstructor
public class AdminSubscriptionServiceImpl implements AdminSubscriptionService {

    private final UserSubscriptionMapper userSubscriptionMapper;
    private final NotificationService notificationService;

    @Override
    public IPage<AdminSubscriptionVO> getSubscriptionPage(AdminSubscriptionQueryDTO query) {
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : Math.min(query.getSize(), 100);
        String status = StringUtils.hasText(query.getStatus()) ? query.getStatus() : null;
        return userSubscriptionMapper.selectAdminSubscriptionPage(new Page<>(page, size), status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pauseSubscription(Long id) {
        UserSubscription subscription = getSubscription(id);
        if (!"ACTIVE".equals(subscription.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "当前状态无法暂停");
        }
        subscription.setStatus("PAUSED");
        userSubscriptionMapper.updateById(subscription);

        notificationService.send(
                subscription.getUserId(),
                "订阅已被管理员暂停",
                "你的订阅已由管理员暂停，暂停期间不会生成新的配送订单。",
                "SUBSCRIPTION"
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSubscription(Long id) {
        UserSubscription subscription = getSubscription(id);
        if ("CANCELLED".equals(subscription.getStatus())) {
            return; // 幂等
        }
        subscription.setStatus("CANCELLED");
        userSubscriptionMapper.updateById(subscription);

        notificationService.send(
                subscription.getUserId(),
                "订阅已被管理员取消",
                "你的订阅已由管理员取消，将不再产生新的订阅订单。",
                "SUBSCRIPTION"
        );
    }

    private UserSubscription getSubscription(Long id) {
        UserSubscription subscription = userSubscriptionMapper.selectById(id);
        if (subscription == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订阅不存在");
        }
        return subscription;
    }
}
