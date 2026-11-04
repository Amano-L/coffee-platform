<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import AddressFormDialog from '@/components/user/AddressFormDialog.vue'
import UserCenterNav from '@/components/user/UserCenterNav.vue'
import { deleteAddress, getAddresses, setDefaultAddress } from '@/api/user'
import type { Address } from '@/types/user'

const addresses = ref<Address[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingAddress = ref<Address | null>(null)

async function loadAddresses(): Promise<void> {
  loading.value = true
  try {
    addresses.value = await getAddresses()
  } catch {
    addresses.value = []
  } finally {
    loading.value = false
  }
}

function openCreate(): void {
  editingAddress.value = null
  dialogVisible.value = true
}

function openEdit(address: Address): void {
  editingAddress.value = address
  dialogVisible.value = true
}

async function handleSaved(): Promise<void> {
  await loadAddresses()
}

async function handleDelete(address: Address): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `确定删除「${address.receiverName}」的收货地址吗？`,
      '删除地址',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' },
    )
  } catch {
    return
  }
  try {
    await deleteAddress(address.id)
    ElMessage.success('地址已删除')
    await loadAddresses()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

async function handleSetDefault(address: Address): Promise<void> {
  if (address.isDefault === 1) return
  try {
    await setDefaultAddress(address.id)
    ElMessage.success('已设为默认地址')
    await loadAddresses()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  }
}

onMounted(() => {
  void loadAddresses()
})
</script>

<template>
  <div class="page">
    <div class="page-container">
      <header class="page-head">
        <div>
          <span class="eyebrow">ADDRESS BOOK</span>
          <h1 class="page-title">收货地址</h1>
          <p class="page-subtitle">管理常用收货地址，结算时默认地址会被优先选中。</p>
        </div>
        <el-button type="primary" @click="openCreate">
          <el-icon><Plus /></el-icon>
          新增地址
        </el-button>
      </header>

      <UserCenterNav />

      <div v-if="loading" class="address-grid">
        <div v-for="n in 2" :key="n" class="surface card-skeleton">
          <el-skeleton :rows="4" animated />
        </div>
      </div>

      <AppEmpty
        v-else-if="addresses.length === 0"
        description="还没有收货地址，添加一个方便下单吧"
        action-text="新增地址"
        show-action
        @action="openCreate"
      />

      <div v-else class="address-grid">
        <article
          v-for="address in addresses"
          :key="address.id"
          class="surface address-card"
          :class="{ default: address.isDefault === 1 }"
        >
          <div class="address-head">
            <div class="name-line">
              <strong>{{ address.receiverName }}</strong>
              <span class="phone">{{ address.phone }}</span>
              <el-tag v-if="address.isDefault === 1" size="small" type="success" effect="light" round>
                默认地址
              </el-tag>
            </div>
            <span class="address-id">#{{ address.id }}</span>
          </div>

          <p class="address-detail">
            {{ address.province }}{{ address.city }}{{ address.district }}
            {{ address.detailAddress }}
          </p>

          <div class="address-actions">
            <el-button text @click="openEdit(address)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              v-if="address.isDefault !== 1"
              text
              @click="handleSetDefault(address)"
            >
              <el-icon><Star /></el-icon>
              设为默认
            </el-button>
            <el-button text type="danger" @click="handleDelete(address)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </article>

        <button type="button" class="add-card" @click="openCreate">
          <el-icon :size="24"><Plus /></el-icon>
          <span>新增收货地址</span>
        </button>
      </div>

      <AddressFormDialog
        v-model="dialogVisible"
        :address="editingAddress"
        @saved="handleSaved"
      />
    </div>
  </div>
</template>

<style scoped>
.address-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.card-skeleton {
  padding: 24px;
}

.address-card {
  display: flex;
  flex-direction: column;
  padding: 22px;
  border-top: 3px solid transparent;
}

.address-card.default {
  border-top-color: var(--caramel);
}

.address-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.name-line {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.name-line strong {
  font-size: 16px;
  color: var(--coffee-900);
}

.phone {
  font-size: 13px;
  color: var(--muted);
}

.address-id {
  font-size: 11px;
  color: #b3a294;
}

.address-detail {
  margin-top: 12px;
  font-size: 13px;
  line-height: 1.75;
  color: var(--coffee-600);
  min-height: 46px;
}

.address-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px dashed var(--line-soft);
  flex-wrap: wrap;
}

.add-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 178px;
  padding: 22px;
  border: 1px dashed var(--line);
  border-radius: 18px;
  background: rgba(255, 253, 249, 0.5);
  color: var(--muted);
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s ease;
}

.add-card:hover {
  border-color: var(--caramel);
  color: var(--caramel-dark);
  background: var(--paper);
}

@media (max-width: 760px) {
  .address-grid {
    grid-template-columns: 1fr;
  }
}
</style>
