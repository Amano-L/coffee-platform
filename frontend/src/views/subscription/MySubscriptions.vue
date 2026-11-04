<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import SubscriptionStatusTag from '@/components/subscription/SubscriptionStatusTag.vue'
import { useSubscriptionStore } from '@/stores/subscription'
import type { UserSubscription } from '@/types/subscription'
import { formatDate, formatDateTime, periodLabel } from '@/utils/format'

const router = useRouter()
const subscriptionStore = useSubscriptionStore()

const subscriptions = computed(() => subscriptionStore.mySubscriptions)
const loading = computed(() => subscriptionStore.myLoading)
const activeCount = computed(
  () => subscriptions.value.filter((item) => item.status === 'ACTIVE').length,
)

async function loadSubscriptions(): Promise<void> {
  try {
    await subscriptionStore.fetchMySubscriptions(true)
  } catch {
    // 错误提示由 Axios 拦截器统一处理
  }
}

async function handlePause(subscription: UserSubscription): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `暂停「${subscription.planName}」后将不再生成新订单，确定暂停吗？`,
      '暂停订阅',
      { confirmButtonText: '暂停', cancelButtonText: '取消', type: 'warning' },
    )
  } catch {
    return
  }
  try {
    await subscriptionStore.pause(subscription.id)
    ElMessage.success('订阅已暂停')
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

async function handleSkip(subscription: UserSubscription): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `跳过后本次配送将顺延一个周期，确定跳过「${subscription.planName}」本次配送吗？`,
      '跳过本次配送',
      { confirmButtonText: '跳过本次', cancelButtonText: '取消', type: 'warning' },
    )
  } catch {
    return
  }
  try {
    await subscriptionStore.skip(subscription.id)
    ElMessage.success('已跳过本次配送')
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

async function handleCancel(subscription: UserSubscription): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `取消「${subscription.planName}」后将不再产生新的订阅订单，确定取消吗？`,
      '取消订阅',
      { confirmButtonText: '取消订阅', cancelButtonText: '再想想', type: 'warning' },
    )
  } catch {
    return
  }
  try {
    await subscriptionStore.cancel(subscription.id)
    ElMessage.success('订阅已取消')
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

onMounted(() => {
  void loadSubscriptions()
})
</script>

<template>
  <div class="page">
    <div class="page-container">
      <header class="page-head">
        <div>
          <span class="eyebrow">MY SUBSCRIPTIONS</span>
          <h1 class="page-title">我的订阅</h1>
          <p class="page-subtitle">管理配送节奏，随时暂停、跳过或取消。</p>
        </div>
        <div class="head-actions">
          <span v-if="subscriptions.length > 0" class="active-chip">
            {{ activeCount }} 个生效中
          </span>
          <el-button plain @click="router.push('/subscriptions/plans')">浏览订阅计划</el-button>
        </div>
      </header>

      <div v-if="loading && subscriptions.length === 0" class="sub-grid">
        <div v-for="n in 2" :key="n" class="surface sub-skeleton">
          <el-skeleton :rows="4" animated />
        </div>
      </div>

      <AppEmpty
        v-else-if="subscriptions.length === 0"
        description="还没有订阅，选一个计划让好咖啡按时抵达"
        action-text="查看订阅计划"
        show-action
        @action="router.push('/subscriptions/plans')"
      />

      <div v-else class="sub-grid">
        <article v-for="subscription in subscriptions" :key="subscription.id" class="surface sub-card">
          <div class="sub-head">
            <div>
              <span class="period">{{ periodLabel(subscription.period) }}配送</span>
              <h2>{{ subscription.planName }}</h2>
            </div>
            <SubscriptionStatusTag :status="subscription.status" />
          </div>

          <div class="sub-info">
            <div>
              <span>下次配送</span>
              <strong>{{ formatDate(subscription.nextDeliveryDate) }}</strong>
            </div>
            <div>
              <span>订阅时间</span>
              <strong>{{ formatDateTime(subscription.createTime) }}</strong>
            </div>
          </div>

          <p v-if="subscription.status === 'ACTIVE'" class="sub-hint">
            订阅生效中，到期会自动生成订单。你可以随时跳过本次配送或暂停。
          </p>
          <p v-else-if="subscription.status === 'PAUSED'" class="sub-hint paused">
            已暂停，不会生成新的订单。如需继续收到咖啡，可以取消后重新订阅。
          </p>
          <p v-else class="sub-hint cancelled">该订阅已取消，不会再产生新的订单。</p>

          <div class="sub-actions">
            <template v-if="subscription.status === 'ACTIVE'">
              <el-button text @click="handleSkip(subscription)">跳过本次</el-button>
              <el-button text @click="handlePause(subscription)">暂停配送</el-button>
              <el-button text type="danger" @click="handleCancel(subscription)">取消订阅</el-button>
            </template>
            <el-button
              v-else-if="subscription.status === 'PAUSED'"
              text
              type="danger"
              @click="handleCancel(subscription)"
            >
              取消订阅
            </el-button>
            <el-button v-else text @click="router.push('/subscriptions/plans')">
              重新订阅
            </el-button>
          </div>
        </article>
      </div>
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

.active-chip {
  padding: 6px 14px;
  border-radius: 999px;
  background: #eef1e7;
  color: #5f6d4e;
  font-size: 12px;
}

.sub-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.sub-skeleton {
  padding: 24px;
}

.sub-card {
  display: flex;
  flex-direction: column;
  padding: 24px;
}

.sub-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.period {
  font-size: 11px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: var(--caramel-dark);
}

.sub-head h2 {
  margin-top: 6px;
  font-size: 22px;
  color: var(--coffee-900);
}

.sub-info {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 20px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #faf5ee;
}

.sub-info div {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.sub-info span {
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 0.06em;
}

.sub-info strong {
  font-size: 14px;
  color: var(--coffee-800);
}

.sub-hint {
  margin-top: 16px;
  font-size: 12px;
  line-height: 1.7;
  color: var(--muted);
}

.sub-hint.paused {
  color: #92703a;
}

.sub-hint.cancelled {
  color: #a8968a;
}

.sub-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: auto;
  padding-top: 18px;
  border-top: 1px dashed var(--line-soft);
}

@media (max-width: 860px) {
  .sub-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 520px) {
  .sub-card {
    padding: 18px;
  }

  .sub-info {
    grid-template-columns: 1fr;
  }
}
</style>
