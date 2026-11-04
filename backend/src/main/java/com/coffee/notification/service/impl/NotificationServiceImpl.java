package com.coffee.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.notification.entity.Notification;
import com.coffee.notification.mapper.NotificationMapper;
import com.coffee.notification.service.NotificationService;
import com.coffee.notification.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 站内通知实现。
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public void send(Long userId, String title, String content, String type) {
        if (userId == null || !StringUtils.hasText(title) || !StringUtils.hasText(content)) {
            return;
        }
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(StringUtils.hasText(type) ? type : "SYSTEM");
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    @Override
    public IPage<NotificationVO> getMyNotifications(Long userId, Integer page, Integer size) {
        int current = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : Math.min(size, 100);
        Page<Notification> notificationPage = new Page<>(current, pageSize);
        IPage<Notification> result = notificationMapper.selectPage(
                notificationPage,
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreateTime)
                        .orderByDesc(Notification::getId)
        );
        return result.convert(this::toVO);
    }

    @Override
    public void markRead(Long userId, Long id) {
        Notification notification = notificationMapper.selectOne(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getId, id)
                        .eq(Notification::getUserId, userId)
        );
        if (notification == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "通知不存在");
        }
        if (notification.getIsRead() != null && notification.getIsRead() == 1) {
            return;
        }
        notification.setIsRead(1);
        notificationMapper.updateById(notification);
    }

    private NotificationVO toVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setTitle(notification.getTitle());
        vo.setContent(notification.getContent());
        vo.setType(notification.getType());
        vo.setIsRead(notification.getIsRead());
        vo.setCreateTime(notification.getCreateTime());
        return vo;
    }
}
