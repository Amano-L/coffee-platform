<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import SubscriptionStatusTag from '@/components/subscription/SubscriptionStatusTag.vue'
import {
  cancelAdminSubscription,
  getAdminSubscriptions,
  pauseAdminSubscription,
} from '@/api/admin'
import { usePagination } from '@/composables/usePagination'
import type { AdminSubscription } from '@/types/admin'
import { SUBSCRIPTION_STATUS_META } from '@/utils/constants'
import { formatDate, formatDateTime, periodLabel } from '@/utils/format'

const subscriptions = ref<AdminSubscription[]>([])
const loading = ref(false)
const errorMessage = ref('')
const statusFilter = ref('')
const actionId = ref<number | null>(null)

const { page, size, total, applyPageResult, handlePageChange, handleSizeChange } =
  usePagination(10)

const statusOptions = [
  { label: '生效中', value: 'ACTIVE' },
  { label: '已暂停', value: 'PAUSED' },
  { label: '已取消', value: 'CANCELLED' },
]

async function fetchSubscriptions(): Promise<void> {
  loading.value = true
  errorMessage.value = ''
  try {
    const result = await getAdminSubscriptions({
      page: page.value,
      size: size.value,
      status: statusFilter.value || undefined,
    })
    subscriptions.value = result.records ?? []
    applyPageResult(result)
  } catch (error) {
    subscriptions.value = []
    errorMessage.value = error instanceof Error ? error.message : '请求失败'
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  page.value = 1
  void fetchSubscriptions()
}

function handleReset(): void {
  statusFilter.value = ''
  page.value = 1
  void fetchSubscriptions()
}

function handleCurrentChange(value: number): void {
  handlePageChange(value)
  void fetchSubscriptions()
}

function handleSizeChangeClick(value: number): void {
  handleSizeChange(value)
  void fetchSubscriptions()
}

async function handlePause(row: AdminSubscription): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `暂停用户 #${row.userId} 的订阅「${row.planName}」后，将不再生成新订单。`,
      '暂停订阅',
      { confirmButtonText: '暂停', cancelButtonText: '取消', type: 'warning' },
    )
  } catch {
    return
  }
  actionId.value = row.id
  try {
    await pauseAdminSubscription(row.id)
    ElMessage.success('订阅已暂停')
    void fetchSubscriptions()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    actionId.value = null
  }
}

async function handleCancel(row: AdminSubscription): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `确定取消用户 #${row.userId} 的订阅「${row.planName}」吗？取消后不可恢复。`,
      '取消订阅',
      { confirmButtonText: '取消订阅', cancelButtonText: '再想想', type: 'warning' },
    )
  } catch {
    return
  }
  actionId.value = row.id
  try {
    await cancelAdminSubscription(row.id)
    ElMessage.success('订阅已取消')
    void fetchSubscriptions()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    actionId.value = null
  }
}

onMounted(() => {
  void fetchSubscriptions()
})
</script>

<template>
  <div class="admin-page">
    <header class="admin-page-head">
      <div>
        <span class="eyebrow">SUBSCRIPTIONS</span>
        <h1>订阅管理</h1>
        <p>查看全部用户订阅，并可暂停或取消异常订阅。</p>
      </div>
      <el-button plain :loading="loading" @click="fetchSubscriptions">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </header>

    <el-alert
      v-if="errorMessage"
      type="error"
      :closable="false"
      show-icon
      title="后台订阅列表加载失败"
      :description="errorMessage"
      class="page-alert"
    >
      <template #default>
        <el-button size="small" type="primary" plain @click="fetchSubscriptions">重新加载</el-button>
      </template>
    </el-alert>

    <el-form class="filter-bar" :inline="true" @submit.prevent="handleSearch">
      <el-form-item label="订阅状态">
        <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width: 150px">
          <el-option
            v-for="option in statusOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">筛选</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <section class="surface table-wrap">
      <el-table v-loading="loading" :data="subscriptions" row-key="id">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="用户" min-width="160">
          <template #default="{ row }">
            <div class="user-cell">
              <strong>{{ row.username || `用户 #${row.userId}` }}</strong>
              <span>ID: {{ row.userId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="planName" label="订阅计划" min-width="160" />
        <el-table-column label="周期" width="100">
          <template #default="{ row }">{{ periodLabel(row.period) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <SubscriptionStatusTag :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column label="下次配送" width="130">
          <template #default="{ row }">{{ formatDate(row.nextDeliveryDate) }}</template>
        </el-table-column>
        <el-table-column label="订阅时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'ACTIVE'">
              <el-button
                text
                :loading="actionId === row.id"
                @click="handlePause(row as AdminSubscription)"
              >
                暂停
              </el-button>
              <el-button text type="danger" @click="handleCancel(row as AdminSubscription)">
                取消
              </el-button>
            </template>
            <el-button
              v-else-if="row.status === 'PAUSED'"
              text
              type="danger"
              :loading="actionId === row.id"
              @click="handleCancel(row as AdminSubscription)"
            >
              取消
            </el-button>
            <span v-else class="muted-note">
              {{ SUBSCRIPTION_STATUS_META.CANCELLED.label }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <AppEmpty
        v-if="!loading && subscriptions.length === 0 && !errorMessage"
        description="暂无订阅记录"
        action-text="刷新"
        show-action
        @action="fetchSubscriptions"
      />
    </section>

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
</template>

<style scoped>
.admin-page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 22px;
  flex-wrap: wrap;
}

.admin-page-head h1 {
  margin-top: 8px;
  font-size: 28px;
  color: var(--coffee-900);
}

.admin-page-head p {
  margin-top: 6px;
  font-size: 13px;
  color: var(--muted);
}

.page-alert {
  margin-bottom: 16px;
}

.table-wrap {
  padding: 8px 8px 16px;
  overflow: hidden;
}

.user-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-cell strong {
  font-size: 14px;
  font-weight: 600;
  color: var(--coffee-900);
}

.user-cell span {
  font-size: 11px;
  color: var(--muted);
}

.muted-note {
  font-size: 12px;
  color: #a8968a;
}
</style>
