import type { PageQuery, PageResult } from '@/types/api'
import type { NotificationItem } from '@/types/notification'
import request from '@/utils/request'

/**
 * 我的通知 GET /notifications (P1)
 * 后端尚未提供时静默失败，由组件展示空态。
 */
export function getNotifications(params: PageQuery): Promise<PageResult<NotificationItem>> {
  return request.get<PageResult<NotificationItem>>('/notifications', { params, silent: true })
}

/** 标记通知已读 PUT /notifications/{id}/read (P1) */
export function markNotificationRead(id: number): Promise<{ id: number; isRead: number }> {
  return request.put<{ id: number; isRead: number }>(`/notifications/${id}/read`, undefined, {
    silent: true,
  })
}
