<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import OrderStatusTag from '@/components/order/OrderStatusTag.vue'
import { getAdminOrders, shipAdminOrder } from '@/api/admin'
import { usePagination } from '@/composables/usePagination'
import type { AdminOrder } from '@/types/admin'
import { formatDateTime, formatPrice } from '@/utils/format'

const orders = ref<AdminOrder[]>([])
const loading = ref(false)
const shippingOrderNo = ref('')
const pageSizes = [10, 20, 50]

const { page, size, total, applyPageResult, handlePageChange, handleSizeChange } =
  usePagination(10)

async function fetchOrders(): Promise<void> {
  loading.value = true
  try {
    const result = await getAdminOrders({ page: page.value, size: size.value })
    orders.value = result.records ?? []
    applyPageResult(result)
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

function handleCurrentChange(value: number): void {
  handlePageChange(value)
  void fetchOrders()
}

function handleSizeChangeClick(value: number): void {
  handleSizeChange(value)
  void fetchOrders()
}

async function handleShip(order: AdminOrder): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `确认订单 ${order.orderNo} 已备货并安排发货吗？`,
      '订单发货',
      { confirmButtonText: '确认发货', cancelButtonText: '取消', type: 'warning' },
    )
  } catch {
    return
  }
  shippingOrderNo.value = order.orderNo
  try {
    await shipAdminOrder(order.orderNo)
    ElMessage.success('订单已发货')
    await fetchOrders()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    shippingOrderNo.value = ''
  }
}

onMounted(() => {
  void fetchOrders()
})
</script>

<template>
  <div class="admin-page">
    <header class="admin-page-head">
      <div>
        <span class="eyebrow">ORDERS</span>
        <h1>订单管理</h1>
        <p>查看全部订单，并为已支付订单执行发货。</p>
      </div>
      <el-button plain :loading="loading" @click="fetchOrders">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </header>

    <section class="surface table-wrap">
      <el-table v-loading="loading" :data="orders" row-key="orderNo">
        <el-table-column prop="orderNo" label="订单号" min-width="200" />
        <el-table-column label="用户 ID" width="100">
          <template #default="{ row }">#{{ row.userId }}</template>
        </el-table-column>
        <el-table-column label="实付金额" width="140" align="right">
          <template #default="{ row }">{{ formatPrice(row.payAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <OrderStatusTag :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime, true) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === '已支付'"
              type="primary"
              text
              :loading="shippingOrderNo === row.orderNo"
              @click="handleShip(row as AdminOrder)"
            >
              发货
            </el-button>
            <span v-else class="muted-note">
              {{ row.status === '待支付' ? '待支付' : '无需发货' }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <AppEmpty
        v-if="!loading && orders.length === 0"
        description="暂无订单"
        action-text="刷新"
        show-action
        @action="fetchOrders"
      />
    </section>

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

.table-wrap {
  padding: 8px 8px 16px;
  overflow: hidden;
}

.muted-note {
  font-size: 12px;
  color: #a8968a;
}
</style>
