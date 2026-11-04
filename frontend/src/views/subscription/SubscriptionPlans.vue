<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { Component } from 'vue'
import { CoffeeCup, Sugar, Van } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import PlanCard from '@/components/subscription/PlanCard.vue'
import { useAuth } from '@/composables/useAuth'
import { useSubscriptionStore } from '@/stores/subscription'
import type { SubscriptionPlan } from '@/types/subscription'
import {
  FLAVOR_TAG_SUGGESTIONS,
  PROCESS_METHOD_OPTIONS,
  ROAST_LEVEL_OPTIONS,
} from '@/utils/constants'

const route = useRoute()
const router = useRouter()
const { ensureLogin, userStore } = useAuth()
const subscriptionStore = useSubscriptionStore()

const plans = ref<SubscriptionPlan[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  planId: null as number | null,
  roastLevel: 'LIGHT',
  flavorTags: [] as string[],
  processMethod: 'WASHED',
})

const rules: FormRules = {
  planId: [{ required: true, message: '请选择订阅计划', trigger: 'change' }],
}

const steps: { icon: Component; title: string; desc: string }[] = [
  { icon: CoffeeCup, title: '选择计划', desc: '按周、双周或每月，选择适合你的配送节奏。' },
  { icon: Sugar, title: '告诉我们偏好', desc: '选择烘焙度、风味与处理法，让每期更合口味。' },
  { icon: Van, title: '按时送达', desc: '到期自动生成订单，新鲜烘焙后送到家。' },
]

async function loadPlans(): Promise<void> {
  loading.value = true
  try {
    plans.value = await subscriptionStore.fetchPlans()
    const queryPlanId = Number(route.query.planId)
    if (Number.isFinite(queryPlanId) && userStore.isLoggedIn) {
      const plan = plans.value.find((item) => item.id === queryPlanId)
      if (plan) openSubscribe(plan)
    }
  } catch {
    plans.value = []
  } finally {
    loading.value = false
  }
}

function openSubscribe(plan: SubscriptionPlan): void {
  if (!ensureLogin()) return
  form.planId = plan.id
  dialogVisible.value = true
}

async function handleSubscribe(): Promise<void> {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  if (!form.planId) return

  submitting.value = true
  try {
    const result = await subscriptionStore.create({
      planId: form.planId,
      roastLevel: form.roastLevel,
      flavorTags: form.flavorTags,
      processMethod: form.processMethod,
    })
    ElMessage.success(`已订阅「${result.planName}」`)
    dialogVisible.value = false
    await router.push('/subscriptions/my')
  } catch {
    // 错误提示由 Axios 拦截器统一处理（如重复订阅）
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  void loadPlans()
})
</script>

<template>
  <div class="plans-page">
    <section class="plans-hero">
      <div class="page-container hero-inner">
        <span class="eyebrow light">COFFEE SUBSCRIPTION</span>
        <h1>把好咖啡，变成习惯。</h1>
        <p>选一个适合你的配送节奏，我们按时把新鲜烘焙送到家。随时暂停、跳过或取消。</p>
        <div class="hero-points">
          <span><el-icon><CircleCheck /></el-icon>无需长期绑定</span>
          <span><el-icon><CircleCheck /></el-icon>可按偏好定制</span>
          <span><el-icon><CircleCheck /></el-icon>随时调整节奏</span>
        </div>
      </div>
    </section>

    <section class="page-container section plans-section">
      <SectionHeader
        eyebrow="CHOOSE YOUR RHYTHM"
        title="订阅计划"
        subtitle="不同周期，同样的新鲜。所有计划都支持暂停、跳过与取消。"
      />

      <div v-if="loading" class="plan-grid">
        <div v-for="n in 3" :key="n" class="surface plan-skeleton">
          <el-skeleton :rows="5" animated />
        </div>
      </div>

      <div v-else-if="plans.length > 0" class="plan-grid">
        <PlanCard
          v-for="plan in plans"
          :key="plan.id"
          :plan="plan"
          :featured="plan.period === 'MONTHLY'"
          button-text="立即订阅"
          @select="openSubscribe"
        />
      </div>

      <AppEmpty
        v-else
        description="订阅计划正在准备中，请稍后再来"
        action-text="刷新"
        show-action
        @action="loadPlans"
      />
    </section>

    <section class="how-section">
      <div class="page-container">
        <SectionHeader eyebrow="HOW IT WORKS" title="三步开始订阅" />
        <div class="steps-grid">
          <article v-for="(step, index) in steps" :key="step.title" class="step-card">
            <span class="step-index">0{{ index + 1 }}</span>
            <span class="step-icon">
              <el-icon><component :is="step.icon" /></el-icon>
            </span>
            <h3>{{ step.title }}</h3>
            <p>{{ step.desc }}</p>
          </article>
        </div>
      </div>
    </section>

    <section class="page-container faq-section">
      <SectionHeader eyebrow="FAQ" title="关于订阅" />
      <el-collapse class="faq-collapse">
        <el-collapse-item title="订阅后可以暂停吗？" name="1">
          可以。在「我的订阅」中点击暂停，订阅将不再生成新订单；需要时可以随时取消。
        </el-collapse-item>
        <el-collapse-item title="如何跳过某一次配送？" name="2">
          在「我的订阅」中选择「跳过本次」，下次配送日期会按订阅周期自动顺延。
        </el-collapse-item>
        <el-collapse-item title="可以随时取消订阅吗？" name="3">
          可以。取消后不会产生新的订阅订单，已生成的订单不受影响。
        </el-collapse-item>
      </el-collapse>
    </section>

    <el-dialog
      v-model="dialogVisible"
      title="订阅咖啡"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="订阅计划" prop="planId">
          <el-select v-model="form.planId" placeholder="请选择计划" style="width: 100%">
            <el-option
              v-for="plan in plans"
              :key="plan.id"
              :label="`${plan.name} · ¥${Number(plan.price).toFixed(2)}`"
              :value="plan.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="烘焙偏好">
          <el-radio-group v-model="form.roastLevel">
            <el-radio-button
              v-for="option in ROAST_LEVEL_OPTIONS"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="风味偏好">
          <el-select
            v-model="form.flavorTags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入风味标签"
            style="width: 100%"
          >
            <el-option v-for="tag in FLAVOR_TAG_SUGGESTIONS" :key="tag" :label="tag" :value="tag" />
          </el-select>
        </el-form-item>

        <el-form-item label="处理法偏好">
          <el-radio-group v-model="form.processMethod">
            <el-radio-button
              v-for="option in PROCESS_METHOD_OPTIONS"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-alert
          type="info"
          :closable="false"
          show-icon
          title="订阅将在创建后立即生效，下次配送日期以系统计算为准。"
        />
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubscribe">
          确认订阅
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.plans-hero {
  position: relative;
  overflow: hidden;
  padding: 68px 0 60px;
  color: #f7f1ea;
  background:
    radial-gradient(circle at 82% 20%, rgba(192, 133, 82, 0.36), transparent 42%),
    radial-gradient(circle at 10% 90%, rgba(192, 133, 82, 0.2), transparent 38%),
    linear-gradient(155deg, #38231c 0%, #24150f 100%);
}

.plans-hero::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(rgba(255, 250, 244, 0.06) 1px, transparent 1px);
  background-size: 28px 28px;
  opacity: 0.6;
}

.hero-inner {
  position: relative;
  z-index: 1;
}

.eyebrow.light {
  color: #dcb995;
}

.hero-inner h1 {
  margin-top: 14px;
  font-size: clamp(32px, 4vw, 48px);
  color: #fffaf4;
}

.hero-inner p {
  max-width: 620px;
  margin-top: 14px;
  font-size: 15px;
  line-height: 1.8;
  color: #c9b6a9;
}

.hero-points {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 26px;
  margin-top: 24px;
  font-size: 13px;
  color: #e5d4c7;
}

.hero-points span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.hero-points .el-icon {
  color: var(--caramel);
}

.plans-section {
  padding-top: 48px;
}

.plan-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 22px;
}

.plan-skeleton {
  padding: 24px;
}

.how-section {
  background: #f2e9de;
  padding: 54px 0;
  border-top: 1px solid var(--line-soft);
  border-bottom: 1px solid var(--line-soft);
}

.steps-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 22px;
}

.step-card {
  position: relative;
  padding: 26px;
  border-radius: 18px;
  background: var(--paper);
  border: 1px solid var(--line-soft);
}

.step-index {
  position: absolute;
  right: 22px;
  top: 18px;
  font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
  font-size: 30px;
  color: #eadbc9;
}

.step-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 13px;
  background: #f6ebdf;
  color: var(--caramel-dark);
  font-size: 22px;
}

.step-card h3 {
  margin-top: 16px;
  font-size: 18px;
  color: var(--coffee-900);
}

.step-card p {
  margin-top: 8px;
  font-size: 13px;
  line-height: 1.75;
  color: var(--muted);
}

.faq-section {
  padding-top: 52px;
  padding-bottom: 64px;
}

.faq-collapse {
  background: transparent;
  border: none;
}

.faq-collapse :deep(.el-collapse-item) {
  margin-bottom: 10px;
  border: 1px solid var(--line-soft);
  border-radius: 12px;
  overflow: hidden;
  background: var(--paper);
}

.faq-collapse :deep(.el-collapse-item__header) {
  padding: 0 18px;
  background: var(--paper);
  border: none;
  font-weight: 500;
  color: var(--coffee-800);
}

.faq-collapse :deep(.el-collapse-item__wrap) {
  background: var(--paper);
  border: none;
}

.faq-collapse :deep(.el-collapse-item__content) {
  padding: 0 18px 16px;
  font-size: 13px;
  line-height: 1.8;
  color: var(--muted);
}

@media (max-width: 900px) {
  .plan-grid,
  .steps-grid {
    grid-template-columns: 1fr;
  }

  .plans-hero {
    padding: 48px 0 42px;
  }
}
</style>
