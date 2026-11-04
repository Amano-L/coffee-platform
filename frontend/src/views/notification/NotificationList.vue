<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import UserCenterNav from '@/components/user/UserCenterNav.vue'
import { getNotifications, markNotificationRead } from '@/api/notification'
import { usePagination } from '@/composables/usePagination'
import { useNotificationStore } from '@/stores/notification'
import type { NotificationItem } from '@/types/notification'
import { formatDateTime } from '@/utils/format'

const items = ref<NotificationItem[]>([])
const loading = ref(false)
const errorMessage = ref('')
const markingId = ref<number | null>(null)
const notificationStore = useNotificationStore()

const { page, size, total, applyPageResult, handlePageChange, handleSizeChange } =
  usePagination(10)

const unreadOnPage = computed(() => items.value.filter((item) => item.isRead === 0).length)

function typeLabel(type?: string | null): string {
  switch (type) {
    case 'ORDER':
      return '订单'
    case 'SUBSCRIPTION':
      return '订阅'
    default:
      return '系统'
  }
}

function typeTagType(type?: string | null): 'warning' | 'success' | 'info' {
  switch (type) {
    case 'ORDER':
      return 'warning'
    case 'SUBSCRIPTION':
      return 'success'
    default:
      return 'info'
  }
}

async function fetchNotifications(): Promise<void> {
  loading.value = true
  errorMessage.value = ''
  try {
    const result = await getNotifications({ page: page.value, size: size.value })
    items.value = result.records ?? []
    applyPageResult(result)
  } catch (error) {
    items.value = []
    errorMessage.value = error instanceof Error ? error.message : '通知加载失败'
  } finally {
    loading.value = false
  }
}

async function handleRead(item: NotificationItem): Promise<void> {
  if (item.isRead === 1) return
  const previous = item.isRead
  item.isRead = 1
  markingId.value = item.id
  try {
    await markNotificationRead(item.id)
    notificationStore.syncRead(item.id)
  } catch (error) {
    item.isRead = previous
    ElMessage.error(error instanceof Error ? error.message : '标记已读失败')
  } finally {
    markingId.value = null
  }
}

function handleCurrentChange(value: number): void {
  handlePageChange(value)
  void fetchNotifications()
}

function handleSizeChangeClick(value: number): void {
  handleSizeChange(value)
  void fetchNotifications()
}

onMounted(() => {
  void fetchNotifications()
})
</script>

<template>
  <div class="page">
    <div class="page-container">
      <header class="page-head">
        <div>
          <span class="eyebrow">NOTIFICATIONS</span>
          <h1 class="page-title">消息通知</h1>
          <p class="page-subtitle">订单与订阅的最新动态都会出现在这里。</p>
        </div>
        <div class="head-actions">
          <span v-if="unreadOnPage > 0" class="unread-chip">当前页 {{ unreadOnPage }} 条未读</span>
          <el-button plain :loading="loading" @click="fetchNotifications">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </header>

      <UserCenterNav />

      <el-alert
        v-if="errorMessage"
        type="error"
        :closable="false"
        show-icon
        title="通知加载失败"
        :description="errorMessage"
        class="page-alert"
      >
        <template #default>
          <el-button size="small" type="primary" plain @click="fetchNotifications">重新加载</el-button>
        </template>
      </el-alert>

      <div v-if="loading" class="surface notify-loading">
        <el-skeleton :rows="6" animated />
      </div>

      <AppEmpty
        v-else-if="items.length === 0 && !errorMessage"
        description="暂无通知，下单或订阅后会有消息提醒"
        action-text="去逛逛"
        show-action
        @action="$router.push('/products')"
      />

      <div v-else-if="items.length > 0" class="notify-list">
        <article
          v-for="item in items"
          :key="item.id"
          class="surface notify-card"
          :class="{ unread: item.isRead === 0 }"
        >
          <div class="notify-icon">
            <el-icon :size="20">
              <component :is="item.type === 'SUBSCRIPTION' ? 'Refresh' : item.type === 'ORDER' ? 'Tickets' : 'Bell'" />
            </el-icon>
          </div>

          <div class="notify-main">
            <div class="notify-top">
              <el-tag :type="typeTagType(item.type)" effect="light" size="small" round>
                {{ typeLabel(item.type) }}
              </el-tag>
              <h2>{{ item.title }}</h2>
              <span v-if="item.isRead === 0" class="unread-dot" aria-label="未读" />
            </div>
            <p class="notify-content">{{ item.content }}</p>
            <span class="notify-time">{{ formatDateTime(item.createTime, true) }}</span>
          </div>

          <div class="notify-action">
            <el-button
              v-if="item.isRead === 0"
              text
              type="primary"
              :loading="markingId === item.id"
              @click="handleRead(item)"
            >
              标记已读
            </el-button>
            <span v-else class="read-label">已读</span>
          </div>
        </article>
      </div>

      <el-pagination
        v-if="total > 0"
        :current-page="page"
        :page-size="size"
        :page-sizes="[10, 20, 50]"
        :total="total"
        background
        layout="total, sizes, prev, pager, next"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChangeClick"
      />
    </div>
  </div>
</template>

<style scoped>
.head-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.unread-chip {
  padding: 6px 14px;
  border-radius: 999px;
  background: #f2e7da;
  color: var(--caramel-dark);
  font-size: 12px;
}

.page-alert {
  margin-bottom: 18px;
}

.notify-loading {
  padding: 26px;
}

.notify-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notify-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px 22px;
  border-left: 3px solid transparent;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.notify-card.unread {
  border-left-color: var(--caramel);
  background: #fffcf8;
}

.notify-card:hover {
  box-shadow: 0 12px 28px rgba(62, 39, 35, 0.07);
}

.notify-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  flex: none;
  border-radius: 12px;
  background: #f6ebdf;
  color: var(--caramel-dark);
}

.notify-main {
  flex: 1;
  min-width: 0;
}

.notify-top {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.notify-top h2 {
  font-size: 16px;
  font-weight: 600;
  color: var(--coffee-900);
  line-height: 1.45;
}

.unread-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--caramel);
}

.notify-content {
  margin-top: 7px;
  font-size: 13px;
  line-height: 1.75;
  color: var(--coffee-600);
}

.notify-time {
  display: inline-block;
  margin-top: 8px;
  font-size: 11px;
  color: #a8968a;
}

.notify-action {
  flex: none;
  padding-top: 2px;
}

.read-label {
  font-size: 12px;
  color: #b3a294;
}

@media (max-width: 640px) {
  .notify-card {
    padding: 16px;
    gap: 12px;
  }

  .notify-icon {
    width: 38px;
    height: 38px;
  }

  .notify-action {
    display: none;
  }
}
</style>
