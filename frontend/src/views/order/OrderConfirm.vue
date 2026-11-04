<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import PriceText from '@/components/common/PriceText.vue'
import ProductImage from '@/components/common/ProductImage.vue'
import AddressFormDialog from '@/components/user/AddressFormDialog.vue'
import { createOrder } from '@/api/order'
import { getAddresses } from '@/api/user'
import { useCart } from '@/composables/useCart'
import type { Address } from '@/types/user'
import { formatPrice, grindTypeLabel } from '@/utils/format'

const router = useRouter()
const { cartStore, loading, checkedItems, checkedCount, checkedTotalAmount } = useCart()

const addresses = ref<Address[]>([])
const addressLoading = ref(false)
const selectedAddressId = ref<number | null>(null)
const addressDialogVisible = ref(false)
const submitting = ref(false)

const selectedAddress = computed(
  () => addresses.value.find((address) => address.id === selectedAddressId.value) ?? null,
)

const pageLoading = computed(() => loading.value || addressLoading.value)

async function loadAddresses(preferDefault = false): Promise<void> {
  addressLoading.value = true
  try {
    const list = await getAddresses()
    addresses.value = list ?? []
    const currentExists = list.some((address) => address.id === selectedAddressId.value)
    if (!currentExists || preferDefault) {
      selectedAddressId.value =
        list.find((address) => address.isDefault === 1)?.id ?? list[0]?.id ?? null
    }
  } catch {
    addresses.value = []
  } finally {
    addressLoading.value = false
  }
}

async function handleSubmit(): Promise<void> {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (checkedItems.value.length === 0) {
    ElMessage.warning('请返回购物车勾选要结算的商品')
    return
  }

  submitting.value = true
  try {
    const result = await createOrder({
      addressId: selectedAddressId.value,
      cartItemIds: checkedItems.value.map((item) => item.id),
      couponId: null,
    })
    ElMessage.success('订单提交成功')
    await cartStore.fetchCart().catch(() => undefined)
    await router.replace(`/orders/${result.orderNo}`)
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    submitting.value = false
  }
}

function handleAddressSaved(): void {
  void loadAddresses(true)
}

onMounted(() => {
  void cartStore.fetchCart().catch(() => undefined)
  void loadAddresses()
})
</script>

<template>
  <div class="page">
    <div class="page-container">
      <nav class="breadcrumb">
        <router-link to="/cart">购物车</router-link>
        <el-icon><ArrowRight /></el-icon>
        <span>确认订单</span>
      </nav>

      <header class="page-head">
        <div>
          <span class="eyebrow">CHECKOUT</span>
          <h1 class="page-title">确认订单</h1>
          <p class="page-subtitle">核对收货地址与商品信息，提交后即可支付。</p>
        </div>
      </header>

      <div v-if="pageLoading" class="confirm-layout">
        <div class="stack">
          <div class="surface loading-card"><el-skeleton :rows="4" animated /></div>
          <div class="surface loading-card"><el-skeleton :rows="5" animated /></div>
        </div>
        <div class="summary-card"><el-skeleton :rows="5" animated /></div>
      </div>

      <AppEmpty
        v-else-if="checkedItems.length === 0"
        description="没有勾选要结算的商品，请先回到购物车选择"
        action-text="返回购物车"
        show-action
        @action="$router.push('/cart')"
      />

      <div v-else class="confirm-layout">
        <div class="stack">
          <section class="surface block">
            <div class="block-head">
              <div>
                <h2>收货地址</h2>
                <p>请选择本次订单的收货地址</p>
              </div>
              <el-button plain @click="addressDialogVisible = true">
                <el-icon><Plus /></el-icon>
                新增地址
              </el-button>
            </div>

            <el-alert
              v-if="addresses.length === 0"
              type="warning"
              :closable="false"
              show-icon
              title="还没有收货地址"
              description="添加一个收货地址后即可提交订单。"
              class="address-alert"
            >
              <template #default>
                <el-button size="small" type="primary" plain @click="addressDialogVisible = true">
                  添加收货地址
                </el-button>
              </template>
            </el-alert>

            <div v-else class="address-list">
              <button
                v-for="address in addresses"
                :key="address.id"
                type="button"
                class="address-card"
                :class="{ active: address.id === selectedAddressId }"
                @click="selectedAddressId = address.id"
              >
                <div class="address-top">
                  <strong>{{ address.receiverName }}</strong>
                  <span class="phone">{{ address.phone }}</span>
                  <el-tag v-if="address.isDefault === 1" size="small" type="success" effect="plain">
                    默认
                  </el-tag>
                  <el-icon v-if="address.id === selectedAddressId" class="check"><CircleCheckFilled /></el-icon>
                </div>
                <p class="address-detail">
                  {{ address.province }}{{ address.city }}{{ address.district }}
                  {{ address.detailAddress }}
                </p>
              </button>
            </div>
          </section>

          <section class="surface block">
            <div class="block-head">
              <div>
                <h2>商品清单</h2>
                <p>共 {{ checkedCount }} 件商品</p>
              </div>
            </div>

            <div class="item-list">
              <article v-for="item in checkedItems" :key="item.id" class="item-row">
                <div class="item-image">
                  <ProductImage :src="item.coverUrl" :alt="item.productName" />
                </div>
                <div class="item-info">
                  <h3>{{ item.productName }}</h3>
                  <p>{{ item.weight }} · {{ grindTypeLabel(item.grindType) }}</p>
                </div>
                <div class="item-price">
                  <span>{{ formatPrice(item.price) }} × {{ item.quantity }}</span>
                  <PriceText :value="Number(item.price) * item.quantity" />
                </div>
              </article>
            </div>
          </section>
        </div>

        <aside class="summary-card confirm-summary">
          <h2>订单汇总</h2>
          <div class="summary-row">
            <span>商品件数</span>
            <strong>{{ checkedCount }} 件</strong>
          </div>
          <div class="summary-row">
            <span>商品金额</span>
            <strong>{{ formatPrice(checkedTotalAmount) }}</strong>
          </div>
          <div class="summary-total">
            <span>应付金额</span>
            <PriceText :value="checkedTotalAmount" />
          </div>

          <div class="ship-to">
            <span class="ship-label">寄送至</span>
            <template v-if="selectedAddress">
              <strong>{{ selectedAddress.receiverName }} · {{ selectedAddress.phone }}</strong>
              <p>
                {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}
                {{ selectedAddress.detailAddress }}
              </p>
            </template>
            <el-text v-else type="warning" size="small">请先选择收货地址</el-text>
          </div>

          <el-button
            type="primary"
            size="large"
            class="submit-btn"
            :loading="submitting"
            :disabled="!selectedAddressId"
            @click="handleSubmit"
          >
            提交订单
          </el-button>
          <p class="summary-tip">提交订单后库存将被预占，请及时完成支付。</p>
        </aside>
      </div>

      <AddressFormDialog v-model="addressDialogVisible" @saved="handleAddressSaved" />
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

.confirm-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 24px;
  align-items: start;
}

.loading-card {
  padding: 24px;
}

.block {
  padding: 24px;
}

.block-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 18px;
}

.block-head h2 {
  font-size: 19px;
  color: var(--coffee-900);
}

.block-head p {
  margin-top: 4px;
  font-size: 12px;
  color: var(--muted);
}

.address-alert {
  margin-top: 4px;
}

.address-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.address-card {
  position: relative;
  padding: 16px;
  text-align: left;
  background: #fdfaf5;
  border: 1px solid var(--line);
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.address-card:hover {
  border-color: var(--caramel);
}

.address-card.active {
  border-color: var(--coffee-800);
  background: #fbf3e9;
  box-shadow: inset 0 0 0 1px var(--coffee-800);
}

.address-top {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.address-top strong {
  font-size: 14px;
  color: var(--coffee-900);
}

.address-top .phone {
  font-size: 12px;
  color: var(--muted);
}

.address-top .check {
  margin-left: auto;
  color: var(--coffee-800);
}

.address-detail {
  margin-top: 8px;
  font-size: 13px;
  line-height: 1.6;
  color: var(--coffee-600);
}

.item-list {
  display: flex;
  flex-direction: column;
}

.item-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px dashed var(--line-soft);
}

.item-row:last-child {
  border-bottom: none;
}

.item-image {
  width: 64px;
  flex: none;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid var(--line-soft);
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-info h3 {
  font-size: 14px;
  color: var(--coffee-900);
  line-height: 1.5;
}

.item-info p {
  margin-top: 3px;
  font-size: 12px;
  color: var(--muted);
}

.item-price {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  font-size: 12px;
  color: var(--muted);
}

.item-price :deep(.price) {
  font-size: 15px;
}

.confirm-summary {
  position: sticky;
  top: 96px;
}

.confirm-summary h2 {
  font-size: 19px;
  color: var(--coffee-900);
  padding-bottom: 14px;
  border-bottom: 1px solid var(--line-soft);
}

.confirm-summary .summary-row {
  margin-top: 10px;
}

.ship-to {
  margin-top: 18px;
  padding: 14px;
  border-radius: 12px;
  background: #faf5ee;
  font-size: 12px;
  color: var(--muted);
}

.ship-label {
  display: block;
  margin-bottom: 6px;
  letter-spacing: 0.08em;
  color: var(--caramel-dark);
}

.ship-to strong {
  color: var(--coffee-800);
}

.ship-to p {
  margin-top: 4px;
  line-height: 1.6;
}

.submit-btn {
  width: 100%;
  margin-top: 18px;
}

.summary-tip {
  margin-top: 12px;
  font-size: 12px;
  color: var(--muted);
  line-height: 1.6;
}

@media (max-width: 1000px) {
  .confirm-layout {
    grid-template-columns: 1fr;
  }

  .confirm-summary {
    position: static;
  }
}

@media (max-width: 640px) {
  .address-list {
    grid-template-columns: 1fr;
  }

  .block {
    padding: 18px;
  }

  .block-head {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
