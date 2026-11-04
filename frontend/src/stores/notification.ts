import { ref } from 'vue'
import { defineStore } from 'pinia'

import { getNotifications, markNotificationRead } from '@/api/notification'
import type { NotificationItem } from '@/types/notification'
import { getToken } from '@/utils/auth'

/**
 * 顶部通知中心共享状态。
 * 列表页仍维护自己的分页数据，但标记已读后会同步这里的未读角标。
 */
export const useNotificationStore = defineStore('notification', () => {
  const items = ref<NotificationItem[]>([])
  const unreadCount = ref(0)
  const loading = ref(false)
  const loaded = ref(false)
  const error = ref(false)

  async function fetchRecent(force = false): Promise<void> {
    if (!getToken()) {
      reset()
      return
    }
    if (loaded.value && !force) return

    loading.value = true
    error.value = false
    try {
      // 取最近 10 条作为预览，同时计算角标未读数
      const result = await getNotifications({ page: 1, size: 10 })
      items.value = result.records ?? []
      unreadCount.value = items.value.filter((item) => item.isRead === 0).length
      loaded.value = true
    } catch {
      items.value = []
      unreadCount.value = 0
      error.value = true
      loaded.value = true
    } finally {
      loading.value = false
    }
  }

  async function markRead(id: number): Promise<void> {
    const item = items.value.find((entry) => entry.id === id)
    const wasUnread = item?.isRead === 0
    if (item) item.isRead = 1
    if (wasUnread) decrementUnread()

    try {
      await markNotificationRead(id)
    } catch {
      if (item) item.isRead = 0
      if (wasUnread) unreadCount.value += 1
      throw new Error('标记已读失败')
    }
  }

  /** 列表页标记成功后同步角标 */
  function syncRead(id: number): void {
    const item = items.value.find((entry) => entry.id === id)
    if (item && item.isRead === 0) {
      item.isRead = 1
      decrementUnread()
      return
    }
    // 不在预览列表中的通知也可能影响角标，保守地重新拉取
    if (!item) {
      void fetchRecent(true)
    }
  }

  function decrementUnread(): void {
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }

  function reset(): void {
    items.value = []
    unreadCount.value = 0
    loading.value = false
    loaded.value = false
    error.value = false
  }

  return {
    items,
    unreadCount,
    loading,
    loaded,
    error,
    fetchRecent,
    markRead,
    syncRead,
    reset,
  }
})
