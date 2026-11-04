<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'

import emptyImage from '@/assets/images/empty-coffee.svg'
import { useNotificationStore } from '@/stores/notification'
import { useUserStore } from '@/stores/user'
import type { NotificationItem } from '@/types/notification'
import { formatDateTime } from '@/utils/format'

const userStore = useUserStore()
const notificationStore = useNotificationStore()
const { items, unreadCount, loading, error } = storeToRefs(notificationStore)
const visible = ref(false)

function notifyTypeLabel(type?: string | null): string {
  switch (type) {
    case 'ORDER':
      return '订单'
    case 'SUBSCRIPTION':
      return '订阅'
    default:
      return '系统'
  }
}

function load(): void {
  void notificationStore.fetchRecent(true)
}

async function read(item: NotificationItem): Promise<void> {
  if (item.isRead === 1) return
  try {
    await notificationStore.markRead(item.id)
  } catch {
    // 静默失败，下次打开时重新拉取
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) void notificationStore.fetchRecent()
})

watch(
  () => userStore.token,
  () => {
    if (userStore.isLoggedIn) void notificationStore.fetchRecent(true)
    else notificationStore.reset()
  },
)

watch(visible, (open) => {
  if (open) void notificationStore.fetchRecent(true)
})
</script>

<template>
  <el-popover
    v-model:visible="visible"
    placement="bottom-end"
    :width="360"
    trigger="click"
    popper-class="notification-popper"
  >
    <template #reference>
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="bell-badge">
        <el-button circle text class="bell-button" aria-label="通知中心">
          <el-icon :size="19"><Bell /></el-icon>
        </el-button>
      </el-badge>
    </template>

    <div class="notify-panel">
      <div class="notify-head">
        <strong>通知中心</strong>
        <el-button text size="small" :loading="loading" @click="load">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>

      <div v-if="loading" class="notify-loading">
        <el-skeleton :rows="3" animated />
      </div>

      <el-empty
        v-else-if="error"
        :image-size="56"
        description="通知加载失败，请稍后重试"
        :image="emptyImage"
      />

      <el-empty v-else-if="items.length === 0" :image-size="56" description="还没有收到通知"
        :image="emptyImage" />

      <template v-else>
        <button
          v-for="item in items"
          :key="item.id"
          type="button"
          class="notify-item"
          :class="{ unread: item.isRead === 0 }"
          @click="read(item)"
        >
          <span class="dot" />
          <span class="notify-content">
            <span class="notify-title-line">
              <span class="notify-type">{{ notifyTypeLabel(item.type) }}</span>
              <span class="notify-title">{{ item.title }}</span>
            </span>
            <span class="notify-text">{{ item.content }}</span>
            <span class="notify-time">{{ formatDateTime(item.createTime) }}</span>
          </span>
        </button>
      </template>

      <router-link to="/notifications" class="notify-all" @click="visible = false">
        查看全部通知
        <el-icon><ArrowRight /></el-icon>
      </router-link>
    </div>
  </el-popover>
</template>

<style scoped>
.bell-badge {
  display: inline-flex;
  align-items: center;
}

.bell-button {
  color: var(--coffee-700);
  font-size: 18px;
}

.bell-button:hover {
  background: #f4ebe1;
  color: var(--coffee-900);
}

.notify-panel {
  display: flex;
  flex-direction: column;
  min-height: 80px;
}

.notify-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--line-soft);
  color: var(--coffee-900);
}

.notify-loading {
  padding: 14px 4px;
}

.notify-item {
  display: flex;
  gap: 10px;
  width: 100%;
  padding: 12px 6px;
  text-align: left;
  background: none;
  border: none;
  border-bottom: 1px solid var(--line-soft);
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s ease;
}

.notify-item:hover {
  background: #faf5ee;
}

.notify-item:last-child {
  border-bottom: none;
}

.dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  margin-top: 7px;
  background: transparent;
  flex: none;
}

.notify-item.unread .dot {
  background: var(--caramel);
}

.notify-content {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.notify-title-line {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}

.notify-type {
  flex: none;
  padding: 1px 6px;
  border-radius: 999px;
  background: #f4ebe1;
  color: var(--caramel-dark);
  font-size: 10px;
  line-height: 16px;
}

.notify-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--coffee-900);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notify-text {
  font-size: 12px;
  color: var(--muted);
  line-height: 1.5;
}

.notify-time {
  font-size: 11px;
  color: #a8968a;
}

.notify-all {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 8px;
  padding-top: 10px;
  border-top: 1px solid var(--line-soft);
  font-size: 12px;
  color: var(--coffee-500);
}

.notify-all:hover {
  color: var(--coffee-900);
}
</style>
