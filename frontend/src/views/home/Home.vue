<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import heroImage from '@/assets/images/hero-coffee.svg'
import AppEmpty from '@/components/common/AppEmpty.vue'
import SectionHeader from '@/components/common/SectionHeader.vue'
import ProductCard from '@/components/product/ProductCard.vue'
import PlanCard from '@/components/subscription/PlanCard.vue'
import { getProductList } from '@/api/product'
import { useSubscriptionStore } from '@/stores/subscription'
import type { ProductSummary } from '@/types/product'
import type { SubscriptionPlan } from '@/types/subscription'

const router = useRouter()
const subscriptionStore = useSubscriptionStore()

const products = ref<ProductSummary[]>([])
const plans = ref<SubscriptionPlan[]>([])
const productsLoading = ref(true)
const plansLoading = ref(true)

async function loadProducts(): Promise<void> {
  productsLoading.value = true
  try {
    const result = await getProductList({ page: 1, size: 4 })
    products.value = result.records ?? []
  } catch {
    products.value = []
  } finally {
    productsLoading.value = false
  }
}

async function loadPlans(): Promise<void> {
  plansLoading.value = true
  try {
    const list = await subscriptionStore.fetchPlans()
    plans.value = list.slice(0, 3)
  } catch {
    plans.value = []
  } finally {
    plansLoading.value = false
  }
}

function goSubscribe(plan: SubscriptionPlan): void {
  void router.push({ path: '/subscriptions/plans', query: { planId: String(plan.id) } })
}

onMounted(() => {
  void loadProducts()
  void loadPlans()
})
</script>

<template>
  <div class="home">
    <section class="hero">
      <div class="page-container hero-grid">
        <div class="hero-copy rise-in">
          <span class="eyebrow">FRESHLY ROASTED · EVERY WEEK</span>
          <h1>
            把世界的风味，<br />
            <span class="accent">装进你的杯子。</span>
          </h1>
          <p class="hero-desc">
            精选单一产区咖啡豆，小批量新鲜烘焙。无论你是手冲爱好者，还是每天一杯的上班族，
            都能在这里找到属于自己的那一口。
          </p>
          <div class="hero-actions">
            <el-button size="large" type="primary" @click="router.push('/products')">
              探索咖啡豆
              <el-icon class="btn-icon"><ArrowRight /></el-icon>
            </el-button>
            <el-button size="large" plain @click="router.push('/subscriptions/plans')">
              了解订阅
            </el-button>
          </div>
          <div class="hero-stats">
            <div>
              <strong>12+</strong>
              <span>全球产区</span>
            </div>
            <div>
              <strong>48h</strong>
              <span>新鲜烘焙</span>
            </div>
            <div>
              <strong>随时</strong>
              <span>暂停 / 跳过</span>
            </div>
          </div>
        </div>

        <div class="hero-art">
          <div class="art-ring" />
          <img :src="heroImage" alt="咖啡豆与咖啡杯插画" />
          <div class="float-card">
            <span class="dot" />
            <div>
              <strong>本周烘焙</strong>
              <span>埃塞俄比亚 · 花香柑橘</span>
            </div>
          </div>
          <div class="float-chip">
            <el-icon><Sunny /></el-icon>
            新鲜直达
          </div>
        </div>
      </div>
    </section>

    <section class="page-container section">
      <SectionHeader
        eyebrow="CURATED FOR YOU"
        title="本周推荐"
        subtitle="从浅烘的花果香到深烘的巧克力调，挑一款开启今天。"
      >
        <template #extra>
          <el-button text @click="router.push('/products')">
            查看全部<el-icon class="btn-icon"><ArrowRight /></el-icon>
          </el-button>
        </template>
      </SectionHeader>

      <div v-if="productsLoading" class="product-grid">
        <div v-for="n in 4" :key="n" class="skeleton-card">
          <el-skeleton animated>
            <template #template>
              <el-skeleton-item variant="image" style="width: 100%; height: 210px" />
              <div style="padding: 16px">
                <el-skeleton-item variant="text" style="width: 40%" />
                <el-skeleton-item variant="h3" style="width: 85%; margin-top: 10px" />
                <el-skeleton-item variant="text" style="width: 55%; margin-top: 12px" />
              </div>
            </template>
          </el-skeleton>
        </div>
      </div>

      <div v-else-if="products.length > 0" class="product-grid">
        <ProductCard v-for="product in products" :key="product.id" :product="product" />
      </div>

      <AppEmpty
        v-else
        description="暂时没有可展示的咖啡豆"
        action-text="刷新看看"
        show-action
        @action="loadProducts"
      />
    </section>

    <section class="subscribe-band">
      <div class="page-container">
        <div class="band-head">
          <div>
            <span class="eyebrow light">COFFEE SUBSCRIPTION</span>
            <h2>把好咖啡，变成习惯。</h2>
            <p>选一个周期，我们按时把新鲜烘焙送到你手上。随时暂停、跳过或取消。</p>
          </div>
          <el-button plain class="band-btn" @click="router.push('/subscriptions/plans')">
            查看全部计划
          </el-button>
        </div>

        <div v-if="plansLoading" class="plan-grid">
          <div v-for="n in 3" :key="n" class="plan-skeleton">
            <el-skeleton :rows="5" animated />
          </div>
        </div>
        <div v-else-if="plans.length > 0" class="plan-grid">
          <PlanCard
            v-for="(plan, index) in plans"
            :key="plan.id"
            :plan="plan"
            :featured="index === 1"
            button-text="选择计划"
            @select="goSubscribe"
          />
        </div>
        <AppEmpty v-else description="订阅计划即将上线" />
      </div>
    </section>

    <section class="page-container section">
      <SectionHeader
        eyebrow="WHY BEAN PICK"
        title="认真对待每一杯"
        subtitle="从产地到杯子，我们把控每一个影响风味的细节。"
      />
      <div class="value-grid">
        <article class="value-card">
          <span class="value-icon"><el-icon><MapLocation /></el-icon></span>
          <h3>单一产区精选</h3>
          <p>只挑选风味清晰、可追溯产区的咖啡豆，保留每一片土地的性格。</p>
        </article>
        <article class="value-card">
          <span class="value-icon"><el-icon><Sunny /></el-icon></span>
          <h3>小批量新鲜烘焙</h3>
          <p>下单后新鲜烘焙，锁住香气与甜感，让你喝到豆子最好的状态。</p>
        </article>
        <article class="value-card">
          <span class="value-icon"><el-icon><Refresh /></el-icon></span>
          <h3>灵活的订阅节奏</h3>
          <p>按周、双周或每月配送，随时暂停、跳过本次或取消，不绑定长期合约。</p>
        </article>
      </div>
    </section>
  </div>
</template>

<style scoped>
.hero {
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(circle at 78% 18%, rgba(192, 133, 82, 0.28), transparent 42%),
    radial-gradient(circle at 8% 85%, rgba(192, 133, 82, 0.14), transparent 38%),
    linear-gradient(165deg, #fbf7f1 0%, #f2e7da 100%);
  border-bottom: 1px solid var(--line-soft);
}

.hero::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(rgba(62, 39, 35, 0.055) 1px, transparent 1px);
  background-size: 26px 26px;
  opacity: 0.5;
  pointer-events: none;
}

.hero-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  align-items: center;
  gap: 40px;
  min-height: 580px;
  padding-top: 56px;
  padding-bottom: 72px;
}

.hero-copy h1 {
  margin-top: 16px;
  font-size: clamp(38px, 4.6vw, 62px);
  line-height: 1.2;
  letter-spacing: -0.01em;
  color: var(--coffee-900);
}

.hero-copy h1 .accent {
  color: var(--caramel-dark);
  position: relative;
}

.hero-desc {
  max-width: 480px;
  margin-top: 20px;
  font-size: 15px;
  line-height: 1.85;
  color: var(--coffee-600);
}

.hero-actions {
  display: flex;
  gap: 14px;
  margin-top: 30px;
  flex-wrap: wrap;
}

.btn-icon {
  margin-left: 4px;
}

.hero-stats {
  display: flex;
  gap: 42px;
  margin-top: 44px;
  padding-top: 24px;
  border-top: 1px solid rgba(62, 39, 35, 0.12);
}

.hero-stats div {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.hero-stats strong {
  font-size: 22px;
  color: var(--coffee-900);
  font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
}

.hero-stats span {
  font-size: 12px;
  color: var(--muted);
  letter-spacing: 0.05em;
}

.hero-art {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 420px;
}

.art-ring {
  position: absolute;
  width: 380px;
  height: 380px;
  border-radius: 50%;
  border: 1px dashed rgba(62, 39, 35, 0.18);
  animation: spin 40s linear infinite;
}

.hero-art::before {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 30%, #fffaf4 0%, #efe0cf 70%);
  box-shadow: 0 30px 70px rgba(62, 39, 35, 0.12);
}

.hero-art img {
  position: relative;
  z-index: 1;
  width: min(88%, 400px);
  filter: drop-shadow(0 24px 36px rgba(62, 39, 35, 0.16));
}

.float-card {
  position: absolute;
  right: 0;
  bottom: 34px;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: rgba(255, 253, 249, 0.94);
  border: 1px solid var(--line);
  border-radius: 14px;
  box-shadow: 0 16px 34px rgba(62, 39, 35, 0.12);
  backdrop-filter: blur(8px);
}

.float-card .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #7a8f60;
  box-shadow: 0 0 0 4px rgba(122, 143, 96, 0.16);
}

.float-card div {
  display: flex;
  flex-direction: column;
  line-height: 1.4;
}

.float-card strong {
  font-size: 12px;
  color: var(--coffee-900);
}

.float-card span {
  font-size: 11px;
  color: var(--muted);
}

.float-chip {
  position: absolute;
  left: 8px;
  top: 56px;
  z-index: 2;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 999px;
  background: var(--coffee-900);
  color: #fffaf4;
  font-size: 12px;
  box-shadow: 0 12px 26px rgba(62, 39, 35, 0.2);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.skeleton-card {
  background: var(--paper);
  border: 1px solid var(--line-soft);
  border-radius: 18px;
  overflow: hidden;
}

.subscribe-band {
  background:
    radial-gradient(circle at 12% 20%, rgba(192, 133, 82, 0.24), transparent 40%),
    radial-gradient(circle at 88% 90%, rgba(192, 133, 82, 0.14), transparent 36%),
    linear-gradient(160deg, #38231c 0%, #24150f 100%);
  color: #f7f1ea;
  padding: 64px 0;
}

.band-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 30px;
  margin-bottom: 34px;
  flex-wrap: wrap;
}

.eyebrow.light {
  color: #dcb995;
}

.band-head h2 {
  margin-top: 12px;
  font-size: clamp(26px, 3vw, 38px);
  color: #fffaf4;
}

.band-head p {
  max-width: 520px;
  margin-top: 10px;
  font-size: 14px;
  color: #c9b6a9;
}

.band-btn {
  --el-button-bg-color: transparent;
  --el-button-border-color: rgba(255, 250, 244, 0.4);
  --el-button-text-color: #fffaf4;
  --el-button-hover-bg-color: rgba(255, 250, 244, 0.12);
  --el-button-hover-border-color: #fffaf4;
  --el-button-hover-text-color: #fffaf4;
}

.plan-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 22px;
}

.plan-skeleton {
  background: rgba(255, 250, 244, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  padding: 24px;
}

.subscribe-band :deep(.el-skeleton__item) {
  background: rgba(255, 255, 255, 0.14);
}

.value-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 22px;
}

.value-card {
  padding: 28px;
  border-radius: 18px;
  background: var(--paper);
  border: 1px solid var(--line-soft);
  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease;
}

.value-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 36px rgba(62, 39, 35, 0.09);
}

.value-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: #f6ebdf;
  color: var(--caramel-dark);
  font-size: 22px;
}

.value-card h3 {
  margin-top: 18px;
  font-size: 19px;
  color: var(--coffee-900);
}

.value-card p {
  margin-top: 10px;
  font-size: 14px;
  line-height: 1.8;
  color: var(--muted);
}

@media (max-width: 960px) {
  .hero-grid {
    grid-template-columns: 1fr;
    gap: 10px;
    min-height: auto;
    padding-top: 44px;
    padding-bottom: 48px;
  }

  .hero-art {
    min-height: 340px;
  }

  .hero-art img {
    width: min(70%, 320px);
  }

  .art-ring {
    width: 300px;
    height: 300px;
  }

  .hero-art::before {
    width: 240px;
    height: 240px;
  }

  .float-chip {
    top: 30px;
  }

  .plan-grid,
  .value-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .hero-copy h1 {
    font-size: 36px;
  }

  .hero-stats {
    gap: 26px;
  }

  .hero-stats strong {
    font-size: 18px;
  }

  .float-card {
    right: 4px;
    bottom: 16px;
  }
}
</style>
