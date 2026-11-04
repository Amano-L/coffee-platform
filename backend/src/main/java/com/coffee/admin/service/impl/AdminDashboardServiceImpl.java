package com.coffee.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.coffee.admin.service.AdminDashboardService;
import com.coffee.admin.vo.DashboardVO;
import com.coffee.order.entity.CoffeeOrder;
import com.coffee.order.mapper.CoffeeOrderMapper;
import com.coffee.subscription.entity.UserSubscription;
import com.coffee.subscription.mapper.UserSubscriptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 后台数据看板实现
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final CoffeeOrderMapper orderMapper;
    private final UserSubscriptionMapper subscriptionMapper;

    @Override
    public DashboardVO getDashboardData() {
        DashboardVO vo = new DashboardVO();

        // 1. 统计订单总数
        vo.setOrderCount(orderMapper.selectCount(null));

        // 2. 统计总销售额（只统计已支付、已发货、已完成的有效订单）
        QueryWrapper<CoffeeOrder> sumWrapper = new QueryWrapper<>();
        sumWrapper.select("SUM(pay_amount) as total")
                .in("status", "已支付", "待发货", "已发货", "已完成");
        List<Map<String, Object>> sumRows = orderMapper.selectMaps(sumWrapper);

        // 处理返回结果（防止没有订单时列表为空或 sum 为 null）
        BigDecimal totalSales = BigDecimal.ZERO;
        if (!sumRows.isEmpty()) {
            Map<String, Object> map = sumRows.get(0);
            if (map != null && map.get("total") != null) {
                totalSales = new BigDecimal(map.get("total").toString());
            }
        }
        vo.setTotalSales(totalSales);

        // 3. 统计订阅总数
        vo.setSubscriptionCount(subscriptionMapper.selectCount(null));

        // 4. 统计活跃订阅数
        vo.setActiveSubscriptionCount(subscriptionMapper.selectCount(
                new LambdaQueryWrapper<UserSubscription>().eq(UserSubscription::getStatus, "ACTIVE")
        ));

        return vo;
    }
}