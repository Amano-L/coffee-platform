<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

import { createPayment, mockPaymentCallback } from '@/api/payment'
import type { Payment } from '@/types/payment'
import { PAYMENT_STATUS_META } from '@/utils/constants'
import { formatDateTime, formatPrice } from '@/utils/format'

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    orderNo: string
  }>(),
  {
    orderNo: '',
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  paid: []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value),
})

const payment = ref<Payment | null>(null)
const loading = ref(false)
const paying = ref(false)
const errorMessage = ref('')

const statusMeta = computed(() =>
  payment.value
    ? (PAYMENT_STATUS_META[payment.value.status] ?? { label: payment.value.status, type: 'info' })
    : { label: '-', type: 'info' as const },
)

async function loadPayment(): Promise<void> {
  payment.value = null
  errorMessage.value = ''
  if (!props.orderNo) return
  loading.value = true
  try {
    payment.value = await createPayment(props.orderNo)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '支付单创建失败'
  } finally {
    loading.value = false
  }
}

async function handleMockPay(): Promise<void> {
  if (!payment.value) return
  paying.value = true
  try {
    await mockPaymentCallback({
      payNo: payment.value.payNo,
      orderNo: payment.value.orderNo,
      amount: payment.value.amount,
      status: 'SUCCESS',
      sign: 'SUCCESS_SIGN',
    })
    ElMessage.success('支付成功')
    emit('paid')
    visible.value = false
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '支付失败')
  } finally {
    paying.value = false
  }
}

watch(
  () => props.modelValue,
  (open) => {
    if (open) void loadPayment()
  },
)
</script>

<template>
  <el-dialog v-model="visible" title="收银台" width="460px" :close-on-click-modal="false">
    <div v-if="loading" class="pay-loading">
      <el-skeleton :rows="4" animated />
    </div>

    <el-alert
      v-else-if="errorMessage"
      :title="errorMessage"
      type="error"
      :closable="false"
      show-icon
    >
      <template #default>
        <el-button size="small" type="primary" plain class="retry" @click="loadPayment">
          重新获取支付单
        </el-button>
      </template>
    </el-alert>

    <div v-else-if="payment" class="receipt">
      <div class="amount">
        <span class="label">应付金额</span>
        <span class="value">{{ formatPrice(payment.amount) }}</span>
      </div>
      <div class="rows">
        <div class="row">
          <span>支付单号</span>
          <strong>{{ payment.payNo }}</strong>
        </div>
        <div class="row">
          <span>订单号</span>
          <strong>{{ payment.orderNo }}</strong>
        </div>
        <div class="row">
          <span>支付状态</span>
          <el-tag :type="statusMeta.type" effect="light" round>{{ statusMeta.label }}</el-tag>
        </div>
        <div v-if="payment.payTime" class="row">
          <span>支付时间</span>
          <strong>{{ formatDateTime(payment.payTime, true) }}</strong>
        </div>
      </div>
      <el-alert type="info" :closable="false" show-icon class="mock-tip">
        当前为学习/演示环境，点击下方按钮会调用后端模拟支付回调完成支付。
      </el-alert>
    </div>

    <template #footer>
      <el-button @click="visible = false">稍后支付</el-button>
      <el-button
        type="primary"
        :loading="paying"
        :disabled="loading || !payment"
        @click="handleMockPay"
      >
        模拟支付成功
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.pay-loading {
  padding: 8px 0 20px;
}

.retry {
  margin-top: 10px;
}

.receipt {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.amount {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 22px;
  border-radius: 14px;
  background: linear-gradient(140deg, #f7eee3 0%, #f2e4d4 100%);
}

.amount .label {
  font-size: 12px;
  letter-spacing: 0.12em;
  color: var(--muted);
}

.amount .value {
  font-size: 32px;
  font-weight: 700;
  color: var(--coffee-900);
  font-variant-numeric: tabular-nums;
}

.rows {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  font-size: 13px;
  color: var(--muted);
}

.row strong {
  color: var(--ink);
  font-weight: 600;
  text-align: right;
  word-break: break-all;
}

.mock-tip {
  font-size: 12px;
}
</style>
