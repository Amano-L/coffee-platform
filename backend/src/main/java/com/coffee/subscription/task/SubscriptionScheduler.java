package com.coffee.subscription.task;

import com.coffee.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订阅定时任务
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;

    // 每天凌晨 2 点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void scanAndGenerateSubOrders() {
        log.info("开始执行每日订阅订单生成任务...");
        try {
            subscriptionService.processExpiredSubscriptions();
            log.info("每日订阅订单生成任务执行完毕");
        } catch (Exception e) {
            log.error("订阅订单生成任务发生异常", e);
        }
    }
}