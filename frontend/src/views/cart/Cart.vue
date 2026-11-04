<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import PriceText from '@/components/common/PriceText.vue'
import ProductImage from '@/components/common/ProductImage.vue'
import { useCart } from '@/composables/useCart'
import type { CartItem } from '@/types/cart'
import { formatPrice, grindTypeLabel } from '@/utils/format'

const router = useRouter()
const { cartStore, items, loading, allChecked, checkedItems, checkedCount, checkedTotalAmount } =
  useCart()

const someChecked = computed(() => checkedItems.value.length > 0 && !allChecked.value)

onMounted(() => {
  void cartStore.fetchCart().catch(() => undefined)
})

async function handleCheck(item: CartItem, checked: boolean): Promise<void> {
  try {
    await cartStore.updateItem(item.id, { checked: checked ? 1 : 0 })
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

async function handleCheckAll(checked: boolean): Promise<void> {
  try {
    await cartStore.toggleAll(checked)
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

async function handleQuantity(item: CartItem, value: number | undefined): Promise<void> {
  if (!value || value === item.quantity) return
  try {
    await cartStore.updateItem(item.id, { quantity: value })
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

async function handleRemove(item: CartItem): Promise<void> {
  try {
    await ElMessageBox.confirm(`确定从购物车移除「${item.productName}」吗？`, '移除商品', {
      confirmButtonText: '移除',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await cartStore.removeItem(item.id)
    ElMessage.success('已移除')
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

async function handleClear(): Promise<void> {
  try {
    await ElMessageBox.confirm('确定清空购物车吗？此操作不可恢复。', '清空购物车', {
      confirmButtonText: '清空',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await cartStore.clearCart()
    ElMessage.success('购物车已清空')
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

function goCheckout(): void {
  if (checkedItems.value.length === 0) {
    ElMessage.warning('请先勾选要结算的商品')
    return
  }
  void router.push('/order/confirm')
}
</script>

<template>
  <div class="page">
    <div class="page-container">
      <header class="page-head">
        <div>
          <span class="eyebrow">YOUR CART</span>
          <h1 class="page-title">购物车</h1>
          <p class="page-subtitle">确认规格与数量，准备好就一起结算。</p>
        </div>
        <span v-if="items.length > 0" class="count-chip">{{ checkedCount }} 件已选</span>
      </header>

      <div v-if="loading && items.length === 0" class="cart-layout">
        <div class="surface cart-loading">
          <el-skeleton :rows="6" animated />
        </div>
        <div class="summary-card">
          <el-skeleton :rows="4" animated />
        </div>
      </div>

      <AppEmpty
        v-else-if="items.length === 0"
        description="购物车还是空的，去挑几款喜欢的咖啡豆吧"
        action-text="去逛逛"
        show-action
        @action="$router.push('/products')"
      />

      <div v-else class="cart-layout">
        <div class="cart-main surface">
          <div class="cart-toolbar">
            <label class="select-all">
              <el-checkbox
                :model-value="allChecked"
                :indeterminate="someChecked"
                @change="(value) => handleCheckAll(Boolean(value))"
              />
              <span>全选</span>
            </label>
            <span class="toolbar-count">共 {{ items.length }} 种商品</span>
            <el-button text type="danger" class="clear-btn" @click="handleClear">
              <el-icon><Delete /></el-icon>
              清空购物车
            </el-button>
          </div>

          <div class="cart-list">
            <article v-for="item in items" :key="item.id" class="cart-row">
              <el-checkbox
                :model-value="item.checked === 1"
                class="row-check"
                @change="(value) => handleCheck(item, Boolean(value))"
              />

              <div class="row-image">
                <ProductImage :src="item.coverUrl" :alt="item.productName" />
              </div>

              <div class="row-info">
                <h3>{{ item.productName }}</h3>
                <p>{{ item.weight }} · {{ grindTypeLabel(item.grindType) }}</p>
                <span class="row-unit">单价 {{ formatPrice(item.price) }}</span>
              </div>

              <div class="row-quantity">
                <el-input-number
                  :model-value="item.quantity"
                  :min="1"
                  :max="99"
                  size="small"
                  @change="(value) => handleQuantity(item, value as number | undefined)"
                />
              </div>

              <div class="row-subtotal">
                <PriceText :value="Number(item.price) * item.quantity" />
              </div>

              <el-button text type="danger" class="row-remove" @click="handleRemove(item)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </article>
          </div>
        </div>

        <aside class="summary-card cart-summary">
          <h2>订单摘要</h2>
          <div class="summary-row">
            <span>已选商品</span>
            <strong>{{ checkedItems.length }} 种 / {{ checkedCount }} 件</strong>
          </div>
          <div class="summary-row">
            <span>商品金额</span>
            <strong>{{ formatPrice(checkedTotalAmount) }}</strong>
          </div>
          <div class="summary-row">
            <span>合计金额</span>
            <strong class="total">{{ formatPrice(checkedTotalAmount) }}</strong>
          </div>
          <p class="summary-tip">实际支付金额以提交订单后为准。</p>
          <el-button
            type="primary"
            size="large"
            class="checkout-btn"
            :disabled="checkedItems.length === 0"
            @click="goCheckout"
          >
            去结算
          </el-button>
          <router-link to="/products" class="continue-link">
            继续挑选咖啡豆
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </aside>
      </div>
    </div>
  </div>
</template>

<style scoped>
.count-chip {
  padding: 6px 14px;
  border-radius: 999px;
  background: #f2e7da;
  color: var(--caramel-dark);
  font-size: 12px;
}

.cart-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 24px;
  align-items: start;
}

.cart-loading {
  padding: 26px;
}

.cart-main {
  overflow: hidden;
}

.cart-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 22px;
  border-bottom: 1px solid var(--line-soft);
  font-size: 13px;
  color: var(--muted);
}

.select-all {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--coffee-800);
}

.toolbar-count {
  margin-left: auto;
}

.clear-btn {
  font-size: 12px;
}

.cart-list {
  display: flex;
  flex-direction: column;
}

.cart-row {
  display: grid;
  grid-template-columns: 26px 88px minmax(0, 1fr) 110px 100px 34px;
  align-items: center;
  gap: 16px;
  padding: 20px 22px;
  border-bottom: 1px solid var(--line-soft);
}

.cart-row:last-child {
  border-bottom: none;
}

.row-image {
  display: block;
  width: 88px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--line-soft);
}

.row-info h3 {
  font-size: 15px;
  font-weight: 600;
  color: var(--coffee-900);
  line-height: 1.5;
}

.row-info p {
  margin-top: 4px;
  font-size: 12px;
  color: var(--muted);
}

.row-unit {
  display: inline-block;
  margin-top: 6px;
  font-size: 12px;
  color: var(--caramel-dark);
}

.row-quantity {
  display: flex;
  justify-content: center;
}

.row-subtotal {
  text-align: right;
}

.row-subtotal :deep(.price) {
  font-size: 16px;
}

.row-remove {
  justify-self: end;
}

.cart-summary {
  position: sticky;
  top: 96px;
}

.cart-summary h2 {
  font-size: 19px;
  color: var(--coffee-900);
  padding-bottom: 14px;
  border-bottom: 1px solid var(--line-soft);
}

.cart-summary .summary-row {
  margin-top: 10px;
}

.cart-summary .total {
  font-size: 18px;
  color: var(--caramel-dark);
}

.summary-tip {
  margin-top: 16px;
  font-size: 12px;
  color: var(--muted);
}

.checkout-btn {
  width: 100%;
  margin-top: 20px;
}

.continue-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 14px;
  font-size: 12px;
  color: var(--coffee-500);
}

.continue-link:hover {
  color: var(--coffee-900);
}

@media (max-width: 1000px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }

  .cart-summary {
    position: static;
  }
}

@media (max-width: 720px) {
  .cart-row {
    grid-template-columns: 24px 72px minmax(0, 1fr) 34px;
    gap: 12px;
    padding: 16px;
  }

  .row-image {
    width: 72px;
  }

  .row-quantity,
  .row-subtotal {
    grid-column: 3 / 5;
    justify-content: flex-start;
    text-align: left;
    margin-top: 6px;
  }

  .row-remove {
    grid-column: 4;
    grid-row: 1;
  }

  .row-info {
    min-width: 0;
  }

  .cart-toolbar {
    padding: 14px 16px;
  }

  .toolbar-count {
    display: none;
  }
}
</style>
