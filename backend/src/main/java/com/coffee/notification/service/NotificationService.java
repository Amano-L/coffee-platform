package com.coffee.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.notification.vo.NotificationVO;

/**
 * 站内通知服务。
 */
public interface NotificationService {

    /**
     * 给指定用户发送一条站内通知。
     *
     * @param userId  接收用户
     * @param title   标题
     * @param content 内容
     * @param type    类型：ORDER/SUBSCRIPTION/SYSTEM
     */
    void send(Long userId, String title, String content, String type);

    IPage<NotificationVO> getMyNotifications(Long userId, Integer page, Integer size);

    void markRead(Long userId, Long id);
}
