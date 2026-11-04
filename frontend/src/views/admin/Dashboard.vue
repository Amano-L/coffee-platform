<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Box, Goods, Money, Refresh, Tickets, TrendCharts } from '@element-plus/icons-vue'

import OrderStatusTag from '@/components/order/OrderStatusTag.vue'
import { getAdminOrders, getDashboard } from '@/api/admin'
import type { AdminOrder, DashboardStats } from '@/types/admin'
import { formatDateTime, formatPrice } from '@/utils/format'

const router = useRouter()

const stats = ref<DashboardStats | null>(null)
const recentOrders = ref<AdminOrder[]>([])
const loading = ref(false)
const recentLoading = ref(false)
const errorMessage = ref('')

const activeRatio = computed(() => {
  if (!stats.value || stats.value.subscriptionCount === 0) return 0
  return Math.round((stats.value.activeSubscriptionCount / stats.value.subscriptionCount) * 100)
})

const statCards = computed(() => [
  {
    label: '总销售额',
    value: stats.value ? formatPrice(stats.value.totalSales) : '—',
    hint: '有效订单累计',
    icon: Money,
    tone: 'amber',
  },
  {
    label: '订单总数',
    value: stats.value ? String(stats.value.orderCount) : '—',
    hint: '全部订单数量',
    icon: Tickets,
    tone: 'brown',
  },
  {
    label: '订阅总数',
    value: stats.value ? String(stats.value.subscriptionCount) : '—',
    hint: '累计订阅数量',
    icon: Refresh,
    tone: 'olive',
  },
  {
    label: '活跃订阅',
    value: stats.value ? String(stats.value.activeSubscriptionCount) : '—',
    hint: '当前生效中',
    icon: TrendCharts,
    tone: 'caramel',
  },
])

async function loadDashboard(): Promise<void> {
  loading.value = true
  errorMessage.value = ''
  try {
    stats.value = await getDashboard()
  } catch (error) {
    stats.value = null
    errorMessage.value = error instanceof Error ? error.message : '看板数据加载失败'
  } finally {
    loading.value = false
  }
}

async function loadRecentOrders(): Promise<void> {
  recentLoading.value = true
  try {
    const result = await getAdminOrders({ page: 1, size: 5 })
    recentOrders.value = result.records ?? []
  } catch {
    recentOrders.value = []
  } finally {
    recentLoading.value = false
  }
}

function refresh(): void {
  void loadDashboard()
  void loadRecentOrders()
}

onMounted(() => {
  refresh()
})
</script>

<template>
  <div class="admin-page">
    <header class="admin-page-head">
      <div>
        <span class="eyebrow">OVERVIEW</span>
        <h1>数据看板</h1>
        <p>掌握销售、订单与订阅的整体表现。</p>
      </div>
      <el-button plain :loading="loading" @click="refresh">
        <el-icon><Refresh /></el-icon>
        刷新数据
      </el-button>
    </header>

    <el-alert
      v-if="errorMessage"
      type="error"
      :closable="false"
      show-icon
      title="看板数据加载失败"
      :description="errorMessage"
      class="page-alert"
    >
      <template #default>
        <el-button size="small" type="primary" plain @click="loadDashboard">重新加载</el-button>
      </template>
    </el-alert>

    <div v-if="loading" class="stat-grid">
      <div v-for="n in 4" :key="n" class="stat-card surface">
        <el-skeleton :rows="2" animated />
      </div>
    </div>

    <div v-else class="stat-grid">
      <article v-for="card in statCards" :key="card.label" class="stat-card surface">
        <div class="stat-top">
          <span class="stat-label">{{ card.label }}</span>
          <span class="stat-icon" :class="card.tone">
            <el-icon><component :is="card.icon" /></el-icon>
          </span>
        </div>
        <strong class="stat-value">{{ card.value }}</strong>
        <span class="stat-hint">{{ card.hint }}</span>
      </article>
    </div>

    <div class="dash-grid">
      <section class="surface panel">
        <div class="panel-head">
          <div>
            <h2>最近订单</h2>
            <p>最新的 5 笔订单</p>
          </div>
          <el-button text @click="router.push('/admin/orders')">全部订单</el-button>
        </div>

        <el-table v-loading="recentLoading" :data="recentOrders" class="dash-table">
          <el-table-column prop="orderNo" label="订单号" min-width="180" />
          <el-table-column label="用户 ID" width="90">
            <template #default="{ row }">#{{ row.userId }}</template>
          </el-table-column>
          <el-table-column label="实付金额" width="120" align="right">
            <template #default="{ row }">{{ formatPrice(row.payAmount) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <OrderStatusTag :status="row.status" />
            </template>
          </el-table-column>
          <el-table-column label="下单时间" width="150">
            <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
          </el-table-column>
        </el-table>

        <el-empty v-if="!recentLoading && recentOrders.length === 0" :image-size="64" description="暂无订单" />
      </section>

      <aside class="side-col">
        <section class="surface panel health">
          <div class="panel-head">
            <div>
              <h2>订阅健康度</h2>
              <p>活跃订阅占比</p>
            </div>
          </div>
          <div class="ratio">
            <span class="ratio-value">{{ activeRatio }}%</span>
            <span class="ratio-label">
              {{ stats?.activeSubscriptionCount ?? 0 }} / {{ stats?.subscriptionCount ?? 0 }} 个订阅生效中
            </span>
          </div>
          <el-progress :percentage="activeRatio" :stroke-width="10" :show-text="false" color="#c08552" />
          <div class="health-note">
            <span><i class="dot active" />生效中</span>
            <span><i class="dot" />已暂停 / 取消</span>
          </div>
        </section>

        <section class="surface panel">
          <div class="panel-head">
            <div>
              <h2>快捷入口</h2>
              <p>常用管理操作</p>
            </div>
          </div>
          <div class="quick-links">
            <button type="button" class="quick-link" @click="router.push('/admin/products')">
              <el-icon><Goods /></el-icon>
              <span>商品管理</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </button>
            <button type="button" class="quick-link" @click="router.push('/admin/inventory')">
              <el-icon><Box /></el-icon>
              <span>库存管理</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </button>
            <button type="button" class="quick-link" @click="router.push('/admin/orders')">
              <el-icon><Tickets /></el-icon>
              <span>订单发货</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </button>
            <button type="button" class="quick-link" @click="router.push('/admin/subscriptions')">
              <el-icon><Refresh /></el-icon>
              <span>订阅管理</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </button>
          </div>
        </section>
      </aside>
    </div>
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
  margin-bottom: 18px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  padding: 20px;
}

.stat-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.stat-label {
  font-size: 13px;
  color: var(--muted);
}

.stat-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  font-size: 18px;
}

.stat-icon.amber {
  background: #f7ead8;
  color: #b27a43;
}

.stat-icon.brown {
  background: #eee4da;
  color: #6d4c41;
}

.stat-icon.olive {
  background: #ecefe4;
  color: #6d7b55;
}

.stat-icon.caramel {
  background: #f5e3d2;
  color: #a86b3c;
}

.stat-value {
  display: block;
  margin-top: 14px;
  font-size: 26px;
  color: var(--coffee-900);
  font-variant-numeric: tabular-nums;
  line-height: 1.2;
}

.stat-hint {
  display: block;
  margin-top: 6px;
  font-size: 11px;
  color: #a8968a;
}

.dash-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) 340px;
  gap: 18px;
  margin-top: 18px;
  align-items: start;
}

.panel {
  padding: 22px;
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.panel-head h2 {
  font-size: 18px;
  color: var(--coffee-900);
}

.panel-head p {
  margin-top: 4px;
  font-size: 12px;
  color: var(--muted);
}

.dash-table {
  margin-top: 6px;
}

.side-col {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.ratio {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 10px;
}

.ratio-value {
  font-size: 34px;
  font-weight: 700;
  color: var(--coffee-900);
  font-variant-numeric: tabular-nums;
}

.ratio-label {
  font-size: 12px;
  color: var(--muted);
}

.health-note {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-top: 14px;
  font-size: 11px;
  color: var(--muted);
}

.health-note span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #ded2c6;
}

.dot.active {
  background: var(--caramel);
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.quick-link {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 11px 12px;
  background: #faf5ee;
  border: 1px solid var(--line-soft);
  border-radius: 10px;
  color: var(--coffee-700);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-link:hover {
  border-color: var(--caramel);
  color: var(--coffee-900);
  background: #f7eee3;
}

.quick-link .arrow {
  margin-left: auto;
  font-size: 12px;
  color: #b3a294;
}

@media (max-width: 1200px) {
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .dash-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .stat-grid {
    grid-template-columns: 1fr;
  }

  .panel {
    padding: 16px;
  }
}
</style>
