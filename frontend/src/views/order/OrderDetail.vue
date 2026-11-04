<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import OrderStatusTag from '@/components/order/OrderStatusTag.vue'
import PaymentDialog from '@/components/order/PaymentDialog.vue'
import PriceText from '@/components/common/PriceText.vue'
import { cancelOrder, getOrderDetail } from '@/api/order'
import type { OrderDetail } from '@/types/order'
import { ORDER_STATUS_STEPS } from '@/utils/constants'
import { formatDateTime, formatPrice } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const order = ref<OrderDetail | null>(null)
const loading = ref(false)
const notFound = ref(false)
const payDialogVisible = ref(false)

const orderNo = computed(() => {
  const raw = route.params.orderNo
  return String(Array.isArray(raw) ? raw[0] : (raw ?? ''))
})

const statusIndex = computed(() => ORDER_STATUS_STEPS.indexOf(order.value?.status ?? ''))
const stepActive = computed(() => (statusIndex.value >= 0 ? statusIndex.value : 0))
const isCancelled = computed(() => order.value?.status === '已取消')
const canPay = computed(() => order.value?.status === '待支付')

async function loadOrder(): Promise<void> {
  if (!orderNo.value) {
    notFound.value = true
    return
  }
  loading.value = true
  notFound.value = false
  try {
    order.value = await getOrderDetail(orderNo.value)
  } catch {
    order.value = null
    notFound.value = true
  } finally {
    loading.value = false
  }
}

async function copyOrderNo(): Promise<void> {
  if (!order.value) return
  try {
    await navigator.clipboard.writeText(order.value.orderNo)
    ElMessage.success('订单号已复制')
  } catch {
    ElMessage.warning('复制失败，请手动选择订单号')
  }
}

function handlePaid(): void {
  void loadOrder()
}

async function handleCancel(): Promise<void> {
  if (!order.value) return
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
    await cancelOrder(order.value.orderNo)
    ElMessage.success('订单已取消')
    void loadOrder()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

watch(
  () => route.params.orderNo,
  () => void loadOrder(),
  { immediate: true },
)
</script>

<template>
  <div class="page">
    <div class="page-container">
      <nav class="breadcrumb">
        <router-link to="/orders">我的订单</router-link>
        <el-icon><ArrowRight /></el-icon>
        <span>订单详情</span>
      </nav>

      <div v-if="loading" class="stack">
        <div class="surface loading-card"><el-skeleton :rows="4" animated /></div>
        <div class="surface loading-card"><el-skeleton :rows="6" animated /></div>
      </div>

      <AppEmpty
        v-else-if="notFound || !order"
        description="订单不存在或无权查看"
        action-text="返回订单列表"
        show-action
        @action="router.push('/orders')"
      />

      <template v-else>
        <header class="detail-head surface">
          <div class="head-main">
            <span class="eyebrow">ORDER DETAIL</span>
            <div class="order-title">
              <h1>{{ order.orderNo }}</h1>
              <el-button text class="copy-btn" @click="copyOrderNo">复制</el-button>
            </div>
            <p>下单时间：{{ formatDateTime(order.createTime) }}</p>
          </div>
          <div class="head-side">
            <OrderStatusTag :status="order.status" />
            <div class="head-actions">
              <el-button v-if="canPay" type="primary" @click="payDialogVisible = true">去支付</el-button>
              <el-button v-if="canPay" plain @click="handleCancel">取消订单</el-button>
            </div>
          </div>
        </header>

        <el-alert
          v-if="isCancelled"
          type="info"
          :closable="false"
          show-icon
          title="该订单已取消"
          description="如已支付，请联系客服处理退款。"
          class="cancel-alert"
        />

        <section v-else class="surface steps-card">
          <el-steps :active="stepActive" finish-status="success" align-center>
            <el-step title="待支付" description="提交订单" />
            <el-step title="已支付" description="支付成功" />
            <el-step title="已发货" description="商品发出" />
            <el-step title="已完成" description="交易完成" />
          </el-steps>
        </section>

        <div class="info-grid">
          <section class="surface info-card">
            <h2>收货信息</h2>
            <div class="info-row">
              <span>收货人</span>
              <strong>{{ order.receiverName }}</strong>
            </div>
            <div class="info-row">
              <span>联系电话</span>
              <strong>{{ order.receiverPhone }}</strong>
            </div>
            <div class="info-row">
              <span>收货地址</span>
              <strong class="address">{{ order.receiverAddress }}</strong>
            </div>
          </section>

          <section class="surface info-card">
            <h2>订单信息</h2>
            <div class="info-row">
              <span>订单编号</span>
              <strong>{{ order.orderNo }}</strong>
            </div>
            <div class="info-row">
              <span>下单时间</span>
              <strong>{{ formatDateTime(order.createTime) }}</strong>
            </div>
            <div class="info-row">
              <span>订单状态</span>
              <OrderStatusTag :status="order.status" />
            </div>
          </section>
        </div>

        <section class="surface items-card">
          <h2>商品清单</h2>
          <el-table :data="order.items" class="items-table">
            <el-table-column prop="productName" label="商品" min-width="220" />
            <el-table-column label="单价" width="130">
              <template #default="{ row }">{{ formatPrice(row.price) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column label="小计" width="140" align="right">
              <template #default="{ row }">
                <span class="subtotal">{{ formatPrice(Number(row.price) * row.quantity) }}</span>
              </template>
            </el-table-column>
          </el-table>

          <div class="amount-summary">
            <div class="amount-line">
              <span>商品金额</span>
              <span>{{ formatPrice(order.totalAmount) }}</span>
            </div>
            <div class="amount-line total">
              <span>实付金额</span>
              <PriceText :value="order.payAmount" />
            </div>
          </div>
        </section>

        <div class="back-row">
          <el-button plain @click="router.push('/orders')">返回订单列表</el-button>
          <el-button text @click="router.push('/products')">继续选购</el-button>
        </div>
      </template>

      <PaymentDialog v-model="payDialogVisible" :order-no="order?.orderNo ?? ''" @paid="handlePaid" />
    </div>
  </div>
</template>

<style scoped>
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 20px;
  font-size: 13px;
  color: var(--muted);
}

.breadcrumb a:hover {
  color: var(--coffee-900);
}

.breadcrumb span {
  color: var(--coffee-700);
}

.loading-card {
  padding: 24px;
}

.detail-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  padding: 26px;
  flex-wrap: wrap;
}

.order-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.order-title h1 {
  font-size: clamp(22px, 2.6vw, 30px);
  color: var(--coffee-900);
  letter-spacing: 0.01em;
  word-break: break-all;
}

.copy-btn {
  font-size: 12px;
  color: var(--muted);
}

.head-main p {
  margin-top: 8px;
  font-size: 12px;
  color: var(--muted);
}

.head-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 14px;
}

.head-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.cancel-alert {
  margin-top: 18px;
}

.steps-card {
  margin-top: 18px;
  padding: 26px 20px 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 18px;
  margin-top: 18px;
}

.info-card {
  padding: 24px;
}

.info-card h2,
.items-card h2 {
  font-size: 18px;
  color: var(--coffee-900);
  padding-bottom: 14px;
  border-bottom: 1px solid var(--line-soft);
  margin-bottom: 8px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 9px 0;
  font-size: 13px;
}

.info-row > span {
  width: 76px;
  flex: none;
  color: var(--muted);
}

.info-row strong {
  font-weight: 500;
  color: var(--coffee-800);
  line-height: 1.6;
}

.address {
  word-break: break-all;
}

.items-card {
  margin-top: 18px;
  padding: 24px;
}

.items-table {
  margin-top: 8px;
  --el-table-header-bg-color: transparent;
}

.subtotal {
  font-weight: 600;
  color: var(--coffee-900);
}

.amount-summary {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px dashed var(--line);
}

.amount-line {
  display: flex;
  align-items: baseline;
  gap: 28px;
  min-width: 240px;
  justify-content: space-between;
  font-size: 13px;
  color: var(--muted);
}

.amount-line.total {
  color: var(--coffee-900);
  font-weight: 600;
}

.amount-line.total :deep(.price) {
  font-size: 24px;
}

.back-row {
  display: flex;
  gap: 10px;
  margin-top: 24px;
}

@media (max-width: 800px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .detail-head {
    padding: 20px;
  }

  .head-side {
    align-items: flex-start;
    width: 100%;
  }

  .items-card {
    padding: 18px;
  }

  .steps-card {
    overflow-x: auto;
  }
}
</style>
