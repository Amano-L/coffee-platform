<script setup lang="ts">
import { computed } from 'vue'

import PriceText from '@/components/common/PriceText.vue'
import type { SubscriptionPlan } from '@/types/subscription'
import { periodLabel } from '@/utils/format'

const props = withDefaults(
  defineProps<{
    plan: SubscriptionPlan
    buttonText?: string
    featured?: boolean
  }>(),
  {
    buttonText: '选择此计划',
    featured: false,
  },
)

const emit = defineEmits<{ select: [plan: SubscriptionPlan] }>()

const unit = computed(() => {
  switch (props.plan.period) {
    case 'WEEKLY':
      return '周'
    case 'BIWEEKLY':
      return '双周'
    case 'MONTHLY':
      return '月'
    default:
      return periodLabel(props.plan.period)
  }
})
</script>

<template>
  <article class="plan-card" :class="{ featured }">
    <div class="plan-top">
      <span class="period">{{ periodLabel(plan.period) }}配送</span>
      <span v-if="featured" class="badge">推荐</span>
    </div>
    <h3 class="plan-name">{{ plan.name }}</h3>
    <div class="plan-price">
      <PriceText :value="plan.price" size="lg" />
      <span class="unit">/ {{ unit }}</span>
    </div>
    <p class="plan-desc">{{ plan.description || '精选当季咖啡豆，按周期送到家。' }}</p>
    <ul class="features">
      <li><el-icon><CircleCheck /></el-icon>随时暂停或跳过配送</li>
      <li><el-icon><CircleCheck /></el-icon>可按偏好挑选风味</li>
      <li><el-icon><CircleCheck /></el-icon>无需长期绑定，随时取消</li>
    </ul>
    <el-button type="primary" :plain="!featured" class="plan-btn" @click="emit('select', plan)">
      {{ buttonText }}
    </el-button>
  </article>
</template>

<style scoped>
.plan-card {
  display: flex;
  flex-direction: column;
  padding: 26px;
  border-radius: 20px;
  background: var(--paper);
  border: 1px solid var(--line-soft);
  box-shadow: 0 10px 30px rgba(62, 39, 35, 0.05);
  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease;
}

.plan-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 40px rgba(62, 39, 35, 0.11);
}

.plan-card.featured {
  background: linear-gradient(160deg, #4a2f26 0%, #2f1c15 100%);
  border-color: transparent;
  color: #f8f3ec;
}

.plan-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.period {
  font-size: 11px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--caramel-dark);
}

.featured .period {
  color: #dcb995;
}

.badge {
  padding: 2px 10px;
  border-radius: 999px;
  background: var(--caramel);
  color: #fff;
  font-size: 11px;
}

.plan-name {
  margin-top: 14px;
  font-size: 24px;
  color: var(--coffee-900);
}

.featured .plan-name {
  color: #fffaf4;
}

.plan-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-top: 10px;
}

.featured .plan-price :deep(.price) {
  color: #fffaf4;
}

.unit {
  font-size: 13px;
  color: var(--muted);
}

.featured .unit {
  color: #cbb3a3;
}

.plan-desc {
  margin-top: 12px;
  min-height: 44px;
  font-size: 14px;
  color: var(--muted);
}

.featured .plan-desc {
  color: #d8c4b6;
}

.features {
  list-style: none;
  margin: 18px 0 22px;
  padding: 18px 0 0;
  border-top: 1px dashed var(--line);
  display: grid;
  gap: 10px;
  font-size: 13px;
  color: var(--coffee-700);
}

.featured .features {
  border-top-color: rgba(255, 255, 255, 0.18);
  color: #e5d4c7;
}

.features li {
  display: flex;
  align-items: center;
  gap: 8px;
}

.features .el-icon {
  color: var(--caramel);
}

.plan-btn {
  margin-top: auto;
  width: 100%;
}

.featured .plan-btn {
  --el-button-bg-color: var(--caramel);
  --el-button-border-color: var(--caramel);
  --el-button-hover-bg-color: #d09764;
  --el-button-hover-border-color: #d09764;
}
</style>
