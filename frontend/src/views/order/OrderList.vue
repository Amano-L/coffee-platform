<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import OrderStatusTag from '@/components/order/OrderStatusTag.vue'
import PaymentDialog from '@/components/order/PaymentDialog.vue'
import { cancelOrder, getOrderList } from '@/api/order'
import { usePagination } from '@/composables/usePagination'
import type { OrderSummary } from '@/types/order'
import { ORDER_STATUS_OPTIONS } from '@/utils/constants'
import { formatDateTime, formatPrice } from '@/utils/format'

const router = useRouter()

const tabs = [
  { label: '全部', value: 'all' },
  ...ORDER_STATUS_OPTIONS.map((option) => ({ label: option.label, value: option.value })),
]

const activeStatus = ref('all')
const orders = ref<OrderSummary[]>([])
const loading = ref(false)
const pageSizes = [5, 10, 20]
const payDialogVisible = ref(false)
const payOrderNo = ref('')

const { page, size, total, applyPageResult, handlePageChange, handleSizeChange } =
  usePagination(10)

async function fetchOrders(): Promise<void> {
  loading.value = true
  try {
    const result = await getOrderList({
      page: page.value,
      size: size.value,
      status: activeStatus.value === 'all' ? undefined : activeStatus.value,
    })
    orders.value = result.records ?? []
    applyPageResult(result)
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

function handleTabChange(): void {
  page.value = 1
  void fetchOrders()
}

function handleCurrentChange(value: number): void {
  handlePageChange(value)
  void fetchOrders()
}

function handleSizeChangeClick(value: number): void {
  handleSizeChange(value)
  void fetchOrders()
}

function goDetail(order: OrderSummary): void {
  void router.push(`/orders/${order.orderNo}`)
}

function openPayment(order: OrderSummary): void {
  payOrderNo.value = order.orderNo
  payDialogVisible.value = true
}

function handlePaid(): void {
  void fetchOrders()
}

async function handleCancel(order: OrderSummary): Promise<void> {
  try {
    await ElMessageBox.confirm('取消后订单占用的库存会被释放，确定取消该订单吗？', '取消订单', {
      confirmButtonText: '取消订单',
      cancelButtonText: '再想想',
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await cancelOrder(order.orderNo)
    ElMessage.success('订单已取消')
    void fetchOrders()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

onMounted(() => {
  void fetchOrders()
})
</script>

<template>
  <div class="page">
    <div class="page-container">
      <header class="page-head">
        <div>
          <span class="eyebrow">MY ORDERS</span>
          <h1 class="page-title">我的订单</h1>
          <p class="page-subtitle">查看订单状态、支付或取消未完成的订单。</p>
        </div>
        <el-button plain @click="router.push('/products')">继续选购</el-button>
      </header>

      <el-tabs v-model="activeStatus" class="status-tabs" @tab-change="handleTabChange">
        <el-tab-pane v-for="tab in tabs" :key="tab.value" :label="tab.label" :name="tab.value" />
      </el-tabs>

      <div v-if="loading" class="order-list">
        <div v-for="n in 3" :key="n" class="surface order-skeleton">
          <el-skeleton :rows="3" animated />
        </div>
      </div>

      <AppEmpty
        v-else-if="orders.length === 0"
        :description="activeStatus === 'all' ? '还没有订单，去挑一款咖啡豆吧' : '该状态下暂无订单'"
        action-text="去选购"
        show-action
        @action="router.push('/products')"
      />

      <div v-else class="order-list">
        <article v-for="order in orders" :key="order.orderNo" class="surface order-card">
          <div class="order-head">
            <div class="order-id">
              <span class="order-no">{{ order.orderNo }}</span>
              <span class="order-time">{{ formatDateTime(order.createTime) }}</span>
            </div>
            <OrderStatusTag :status="order.status" />
          </div>

          <div class="order-body">
            <div class="order-amounts">
              <div>
                <span>订单金额</span>
                <strong>{{ formatPrice(order.totalAmount) }}</strong>
              </div>
              <div>
                <span>实付金额</span>
                <strong class="pay">{{ formatPrice(order.payAmount) }}</strong>
              </div>
            </div>

            <div class="order-actions">
              <el-button text @click="goDetail(order)">查看详情</el-button>
              <el-button v-if="order.status === '待支付'" type="primary" @click="openPayment(order)">
                去支付
              </el-button>
              <el-button v-if="order.status === '待支付'" plain @click="handleCancel(order)">
                取消订单
              </el-button>
            </div>
          </div>
        </article>
      </div>

      <el-pagination
        v-if="total > 0"
        :current-page="page"
        :page-size="size"
        :page-sizes="pageSizes"
        :total="total"
        background
        layout="total, sizes, prev, pager, next"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChangeClick"
      />

      <PaymentDialog v-model="payDialogVisible" :order-no="payOrderNo" @paid="handlePaid" />
    </div>
  </div>
</template>

<style scoped>
.status-tabs {
  margin-bottom: 8px;
}

.status-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.status-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background: var(--line);
}

.version-tabs :deep(.el-tabs__item) {
  color: var(--muted);
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-skeleton {
  padding: 24px;
}

.order-card {
  padding: 20px 24px;
}

.order-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--line-soft);
}

.order-id {
  display: flex;
  align-items: baseline;
  gap: 14px;
  flex-wrap: wrap;
}

.order-no {
  font-size: 14px;
  font-weight: 600;
  color: var(--coffee-900);
  letter-spacing: 0.02em;
}

.order-time {
  font-size: 12px;
  color: var(--muted);
}

.order-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding-top: 16px;
  flex-wrap: wrap;
}

.order-amounts {
  display: flex;
  gap: 40px;
}

.order-amounts div {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.order-amounts span {
  font-size: 12px;
  color: var(--muted);
}

.order-amounts strong {
  font-size: 16px;
  color: var(--coffee-800);
  font-variant-numeric: tabular-nums;
}

.order-amounts .pay {
  color: var(--caramel-dark);
  font-size: 18px;
}

.order-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

@media (max-width: 640px) {
  .order-card {
    padding: 16px;
  }

  .order-amounts {
    gap: 24px;
  }

  .order-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
