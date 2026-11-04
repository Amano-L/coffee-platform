<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import ProductImage from '@/components/common/ProductImage.vue'
import PriceText from '@/components/common/PriceText.vue'
import { getProductDetail } from '@/api/product'
import { useAuth } from '@/composables/useAuth'
import { useCartStore } from '@/stores/cart'
import type { ProductDetail, Sku } from '@/types/product'
import {
  grindTypeLabel,
  parseFlavorTags,
  processMethodLabel,
  roastLevelLabel,
} from '@/utils/format'

const route = useRoute()
const { ensureLogin } = useAuth()
const cartStore = useCartStore()

const product = ref<ProductDetail | null>(null)
const loading = ref(false)
const notFound = ref(false)
const selectedSkuId = ref<number | null>(null)
const quantity = ref(1)
const adding = ref(false)

const flavorTags = computed(() => parseFlavorTags(product.value?.flavorTags))
const selectedSku = computed<Sku | null>(
  () => product.value?.skus.find((sku) => sku.id === selectedSkuId.value) ?? null,
)
const stock = computed(() => selectedSku.value?.stock ?? 0)

async function loadProduct(): Promise<void> {
  const raw = route.params.id
  const id = Number(Array.isArray(raw) ? raw[0] : raw)
  if (!Number.isFinite(id) || id <= 0) {
    notFound.value = true
    return
  }

  loading.value = true
  notFound.value = false
  try {
    const data = await getProductDetail(id)
    product.value = data
    const firstAvailable = data.skus?.find((sku) => sku.stock > 0) ?? data.skus?.[0]
    selectedSkuId.value = firstAvailable?.id ?? null
    quantity.value = 1
  } catch {
    product.value = null
    notFound.value = true
  } finally {
    loading.value = false
  }
}

function selectSku(sku: Sku): void {
  if (sku.stock <= 0) return
  selectedSkuId.value = sku.id
  if (quantity.value > sku.stock) quantity.value = sku.stock
}

async function addToCart(): Promise<void> {
  if (!ensureLogin()) return
  const sku = selectedSku.value
  if (!sku) {
    ElMessage.warning('请先选择商品规格')
    return
  }
  if (sku.stock <= 0) {
    ElMessage.warning('该规格暂时缺货')
    return
  }
  adding.value = true
  try {
    await cartStore.addItem(sku.id, quantity.value)
    ElMessage.success('已加入购物车')
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    adding.value = false
  }
}

watch(
  () => route.params.id,
  () => void loadProduct(),
  { immediate: true },
)
</script>

<template>
  <div class="page">
    <div class="page-container">
      <nav class="breadcrumb">
        <router-link to="/">首页</router-link>
        <el-icon><ArrowRight /></el-icon>
        <router-link to="/products">咖啡豆</router-link>
        <el-icon><ArrowRight /></el-icon>
        <span>{{ product?.name || '商品详情' }}</span>
      </nav>

      <div v-if="loading" class="detail-grid">
        <el-skeleton animated>
          <template #template>
            <el-skeleton-item variant="image" style="width: 100%; height: 480px; border-radius: 20px" />
          </template>
        </el-skeleton>
        <div class="skeleton-info">
          <el-skeleton :rows="8" animated />
        </div>
      </div>

      <AppEmpty
        v-else-if="notFound || !product"
        description="这款咖啡豆不存在或已下架"
        action-text="返回商品列表"
        show-action
        @action="$router.push('/products')"
      />

      <template v-else>
        <div class="detail-grid">
          <div class="gallery">
            <div class="gallery-main">
              <ProductImage :src="product.coverUrl" :alt="product.name" ratio="1 / 1" />
            </div>
            <div class="gallery-notes">
              <span><el-icon><Sunny /></el-icon>新鲜烘焙</span>
              <span><el-icon><Box /></el-icon>产地直采</span>
              <span><el-icon><Van /></el-icon>快速发货</span>
            </div>
          </div>

          <div class="info">
            <span class="eyebrow">{{ product.origin || 'SINGLE ORIGIN' }}</span>
            <h1 class="title">{{ product.name }}</h1>

            <div class="tags">
              <el-tag
                v-for="tag in flavorTags"
                :key="tag"
                type="warning"
                effect="plain"
                round
                size="small"
              >
                {{ tag }}
              </el-tag>
            </div>

            <div class="price-row">
              <PriceText :value="selectedSku?.price ?? 0" size="lg" />
              <span class="stock" :class="{ empty: stock <= 0 }">
                {{ stock > 0 ? `库存 ${stock} 件` : '暂时缺货' }}
              </span>
            </div>

            <div class="meta-grid">
              <div class="meta-item">
                <span>烘焙度</span>
                <strong>{{ roastLevelLabel(product.roastLevel) }}</strong>
              </div>
              <div class="meta-item">
                <span>处理法</span>
                <strong>{{ processMethodLabel(product.processMethod) }}</strong>
              </div>
              <div class="meta-item">
                <span>产区</span>
                <strong>{{ product.origin || '未标注' }}</strong>
              </div>
            </div>

            <div class="option-block">
              <div class="option-head">
                <span>选择规格</span>
                <small v-if="selectedSku">
                  {{ selectedSku.weight }} · {{ grindTypeLabel(selectedSku.grindType) }}
                </small>
              </div>
              <div class="sku-list">
                <button
                  v-for="sku in product.skus"
                  :key="sku.id"
                  type="button"
                  class="sku-card"
                  :class="{ active: sku.id === selectedSkuId, disabled: sku.stock <= 0 }"
                  :disabled="sku.stock <= 0"
                  @click="selectSku(sku)"
                >
                  <span class="sku-weight">{{ sku.weight }}</span>
                  <span class="sku-grind">{{ grindTypeLabel(sku.grindType) }}</span>
                  <span class="sku-price">¥{{ Number(sku.price).toFixed(2) }}</span>
                  <span class="sku-stock">{{ sku.stock > 0 ? `库存 ${sku.stock}` : '缺货' }}</span>
                </button>
              </div>
            </div>

            <div class="quantity-row">
              <span class="label">数量</span>
              <el-input-number v-model="quantity" :min="1" :max="Math.max(stock, 1)" />
              <span class="hint">数量不超过当前库存</span>
            </div>

            <div class="actions">
              <el-button
                type="primary"
                size="large"
                class="add-btn"
                :loading="adding"
                :disabled="stock <= 0"
                @click="addToCart"
              >
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button size="large" plain @click="$router.push('/cart')">查看购物车</el-button>
            </div>
          </div>
        </div>

        <section class="description surface">
          <div class="desc-head">
            <span class="eyebrow">TASTING NOTES</span>
            <h2>风味描述</h2>
          </div>
          <p>{{ product.description || '这款咖啡豆的详细风味描述正在补充中。' }}</p>
          <div class="desc-tags">
            <span v-for="tag in flavorTags" :key="tag">{{ tag }}</span>
          </div>
        </section>
      </template>
    </div>
  </div>
</template>

<style scoped>
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 24px;
  font-size: 13px;
  color: var(--muted);
  flex-wrap: wrap;
}

.breadcrumb a:hover {
  color: var(--coffee-900);
}

.breadcrumb span {
  color: var(--coffee-700);
}

.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.02fr) minmax(0, 0.98fr);
  gap: 46px;
  align-items: start;
}

.gallery {
  position: sticky;
  top: 96px;
}

.gallery-main {
  border-radius: 22px;
  overflow: hidden;
  border: 1px solid var(--line-soft);
  background: var(--paper);
  box-shadow: 0 18px 44px rgba(62, 39, 35, 0.08);
}

.gallery-notes {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 22px;
  margin-top: 16px;
  padding: 14px 18px;
  border-radius: 12px;
  background: #f5ece2;
  color: var(--coffee-600);
  font-size: 12px;
}

.gallery-notes span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.skeleton-info {
  padding-top: 8px;
}

.info {
  padding-top: 6px;
}

.title {
  margin-top: 12px;
  font-size: clamp(30px, 3.5vw, 42px);
  line-height: 1.25;
  color: var(--coffee-900);
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-top: 24px;
  padding-bottom: 22px;
  border-bottom: 1px dashed var(--line);
}

.stock {
  font-size: 12px;
  color: #6f7f58;
  background: #eef1e7;
  padding: 3px 10px;
  border-radius: 999px;
}

.stock.empty {
  color: var(--el-color-danger);
  background: #faeceb;
}

.meta-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 22px;
}

.meta-item {
  padding: 12px 14px;
  border-radius: 12px;
  background: #fbf6ef;
  border: 1px solid var(--line-soft);
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.meta-item span {
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 0.06em;
}

.meta-item strong {
  font-size: 14px;
  color: var(--coffee-800);
}

.option-block {
  margin-top: 26px;
}

.option-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--coffee-800);
}

.option-head small {
  font-weight: 400;
  color: var(--muted);
}

.sku-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}

.sku-card {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 12px 14px;
  text-align: left;
  background: var(--paper);
  border: 1px solid var(--line);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.sku-card:hover {
  border-color: var(--caramel);
}

.sku-card.active {
  border-color: var(--coffee-800);
  background: #fbf3e9;
  box-shadow: inset 0 0 0 1px var(--coffee-800);
}

.sku-card.disabled {
  opacity: 0.48;
  cursor: not-allowed;
}

.sku-weight {
  font-size: 14px;
  font-weight: 600;
  color: var(--coffee-900);
}

.sku-grind {
  font-size: 11px;
  color: var(--muted);
}

.sku-price {
  margin-top: 4px;
  font-size: 14px;
  font-weight: 600;
  color: var(--caramel-dark);
}

.sku-stock {
  font-size: 11px;
  color: var(--muted);
}

.quantity-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 26px;
  font-size: 14px;
  color: var(--coffee-800);
}

.quantity-row .label {
  font-weight: 600;
}

.quantity-row .hint {
  font-size: 12px;
  color: var(--muted);
}

.actions {
  display: flex;
  gap: 12px;
  margin-top: 28px;
  flex-wrap: wrap;
}

.add-btn {
  min-width: 190px;
}

.description {
  margin-top: 56px;
  padding: 34px;
}

.desc-head h2 {
  margin-top: 8px;
  font-size: 26px;
  color: var(--coffee-900);
}

.description p {
  margin-top: 16px;
  max-width: 760px;
  line-height: 1.9;
  color: var(--coffee-600);
}

.desc-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 20px;
}

.desc-tags span {
  padding: 4px 12px;
  border-radius: 999px;
  background: #f6ede2;
  color: #7a5a42;
  font-size: 12px;
}

@media (max-width: 960px) {
  .detail-grid {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .gallery {
    position: static;
  }

  .description {
    padding: 24px;
    margin-top: 36px;
  }
}

@media (max-width: 560px) {
  .meta-grid {
    grid-template-columns: 1fr;
  }

  .actions .el-button {
    width: 100%;
    margin-left: 0;
  }

  .add-btn {
    min-width: 0;
  }
}
</style>
