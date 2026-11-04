package com.coffee.notification.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.common.result.Result;
import com.coffee.common.util.SecurityUtil;
import com.coffee.notification.service.NotificationService;
import com.coffee.notification.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 站内通知接口。
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 我的通知
    @GetMapping
    public Result<IPage<NotificationVO>> getMyNotifications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(
                notificationService.getMyNotifications(SecurityUtil.getCurrentUserId(), page, size)
        );
    }

    // 标记已读
    @PutMapping("/{id}/read")
    public Result<Map<String, Object>> markRead(@PathVariable Long id) {
        notificationService.markRead(SecurityUtil.getCurrentUserId(), id);
        return Result.success(Map.of("id", id, "isRead", 1));
    }
}
