<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import { adjustStock, getInventoryLogs, getInventorySkus } from '@/api/inventory'
import { usePagination } from '@/composables/usePagination'
import type { InventoryLog, InventorySku } from '@/types/inventory'
import { formatDateTime, formatPrice, grindTypeLabel } from '@/utils/format'

interface AdjustForm {
  changeNum: number
  reason: string
}

const LOW_STOCK_THRESHOLD = 10

const rows = ref<InventorySku[]>([])
const loading = ref(false)
const errorMessage = ref('')
const keyword = ref('')

const { page, size, total, applyPageResult, handlePageChange, handleSizeChange } =
  usePagination(10)

const adjustVisible = ref(false)
const adjustSubmitting = ref(false)
const selectedSku = ref<InventorySku | null>(null)
const adjustFormRef = ref<FormInstance>()
const adjustForm = reactive<AdjustForm>({ changeNum: 0, reason: '' })

const logsVisible = ref(false)
const logsLoading = ref(false)
const logs = ref<InventoryLog[]>([])
const logSku = ref<InventorySku | null>(null)
const logPage = ref(1)
const logSize = ref(10)
const logTotal = ref(0)

const adjustRules: FormRules<AdjustForm> = {
  changeNum: [
    {
      validator: (_rule, value, callback) => {
        if (!value || Number(value) === 0) callback(new Error('调整数量不能为 0'))
        else if (!Number.isInteger(Number(value))) callback(new Error('调整数量必须是整数'))
        else callback()
      },
      trigger: 'change',
    },
  ],
  reason: [{ required: true, message: '请填写调整原因', trigger: 'blur' }],
}

const targetStock = computed(() => {
  if (!selectedSku.value) return 0
  return selectedSku.value.stock + Number(adjustForm.changeNum || 0)
})

const typeLabels: Record<string, string> = {
  PRESERVE: '预占',
  CONFIRM: '扣减',
  RELEASE: '释放',
  ADJUST: '调整',
}

function stockTagType(stock: number): 'danger' | 'warning' | 'success' {
  if (stock <= 0) return 'danger'
  if (stock <= LOW_STOCK_THRESHOLD) return 'warning'
  return 'success'
}

async function fetchSkus(): Promise<void> {
  loading.value = true
  errorMessage.value = ''
  try {
    const result = await getInventorySkus({
      page: page.value,
      size: size.value,
      keyword: keyword.value.trim() || undefined,
    })
    rows.value = result.records ?? []
    applyPageResult(result)
  } catch (error) {
    rows.value = []
    errorMessage.value = error instanceof Error ? error.message : '库存加载失败'
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  page.value = 1
  void fetchSkus()
}

function handleReset(): void {
  keyword.value = ''
  page.value = 1
  void fetchSkus()
}

function handleCurrentChange(value: number): void {
  handlePageChange(value)
  void fetchSkus()
}

function handleSizeChangeClick(value: number): void {
  handleSizeChange(value)
  void fetchSkus()
}

function openAdjust(row: InventorySku): void {
  selectedSku.value = row
  adjustForm.changeNum = 0
  adjustForm.reason = ''
  adjustVisible.value = true
}

async function submitAdjust(): Promise<void> {
  if (!selectedSku.value || !adjustFormRef.value) return
  try {
    await adjustFormRef.value.validate()
  } catch {
    return
  }
  if (targetStock.value < 0) {
    ElMessage.warning('调整后库存不能小于 0')
    return
  }

  adjustSubmitting.value = true
  try {
    await adjustStock(selectedSku.value.skuId, {
      changeNum: Number(adjustForm.changeNum),
      reason: adjustForm.reason.trim(),
    })
    ElMessage.success('库存已调整')
    adjustVisible.value = false
    void fetchSkus()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    adjustSubmitting.value = false
  }
}

async function fetchLogs(): Promise<void> {
  if (!logSku.value) return
  logsLoading.value = true
  try {
    const result = await getInventoryLogs({
      skuId: logSku.value.skuId,
      page: logPage.value,
      size: logSize.value,
    })
    logs.value = result.records ?? []
    logTotal.value = result.total ?? 0
  } catch {
    logs.value = []
    logTotal.value = 0
  } finally {
    logsLoading.value = false
  }
}

function openLogs(row: InventorySku): void {
  logSku.value = row
  logPage.value = 1
  logs.value = []
  logTotal.value = 0
  logsVisible.value = true
  void fetchLogs()
}

function handleLogPageChange(value: number): void {
  logPage.value = value
  void fetchLogs()
}

function handleLogSizeChange(value: number): void {
  logSize.value = value
  logPage.value = 1
  void fetchLogs()
}

onMounted(() => {
  void fetchSkus()
})
</script>

<template>
  <div class="admin-page">
    <header class="admin-page-head">
      <div>
        <span class="eyebrow">INVENTORY</span>
        <h1>库存管理</h1>
        <p>查看每个 SKU 的实时库存，手动调整并追踪库存流水。</p>
      </div>
      <el-button plain :loading="loading" @click="fetchSkus">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </header>

    <el-alert
      v-if="errorMessage"
      type="error"
      :closable="false"
      show-icon
      title="库存列表加载失败"
      :description="errorMessage"
      class="page-alert"
    >
      <template #default>
        <el-button size="small" type="primary" plain @click="fetchSkus">重新加载</el-button>
      </template>
    </el-alert>

    <el-form class="filter-bar" :inline="true" @submit.prevent="handleSearch">
      <el-form-item label="关键词">
        <el-input
          v-model="keyword"
          placeholder="商品名称 / 产区 / 规格"
          clearable
          style="width: 240px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
      <div class="legend">
        <span><i class="dot danger" />缺货</span>
        <span><i class="dot warning" />低库存（≤{{ LOW_STOCK_THRESHOLD }}）</span>
        <span><i class="dot success" />正常</span>
      </div>
    </el-form>

    <section class="surface table-wrap">
      <el-table v-loading="loading" :data="rows" row-key="skuId">
        <el-table-column prop="skuId" label="SKU ID" width="90" />
        <el-table-column label="商品" min-width="210">
          <template #default="{ row }">
            <div class="product-cell">
              <strong>{{ row.productName }}</strong>
              <span>{{ row.origin || '未标注产区' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="规格" width="150">
          <template #default="{ row }">{{ row.weight }} · {{ grindTypeLabel(row.grindType) }}</template>
        </el-table-column>
        <el-table-column label="单价" width="110" align="right">
          <template #default="{ row }">{{ formatPrice(row.price) }}</template>
        </el-table-column>
        <el-table-column label="库存" width="110">
          <template #default="{ row }">
            <el-tag :type="stockTagType(row.stock)" effect="light" round>
              {{ row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="openAdjust(row as InventorySku)">
              调整库存
            </el-button>
            <el-button text @click="openLogs(row as InventorySku)">流水</el-button>
          </template>
        </el-table-column>
      </el-table>

      <AppEmpty
        v-if="!loading && rows.length === 0 && !errorMessage"
        description="没有符合条件的 SKU"
        action-text="刷新"
        show-action
        @action="fetchSkus"
      />
    </section>

    <el-pagination
      v-if="total > 0"
      :current-page="page"
      :page-size="size"
      :page-sizes="[10, 20, 50]"
      :total="total"
      background
      layout="total, sizes, prev, pager, next"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChangeClick"
    />

    <el-dialog v-model="adjustVisible" title="调整库存" width="480px" :close-on-click-modal="false">
      <div v-if="selectedSku" class="adjust-summary">
        <strong>{{ selectedSku.productName }}</strong>
        <span>{{ selectedSku.weight }} · {{ grindTypeLabel(selectedSku.grindType) }}</span>
      </div>

      <el-form ref="adjustFormRef" :model="adjustForm" :rules="adjustRules" label-position="top">
        <el-form-item label="调整数量">
          <el-input-number v-model="adjustForm.changeNum" :step="1" style="width: 100%" />
          <p class="form-hint">正数入库，负数出库；当前库存 {{ selectedSku?.stock ?? 0 }}。</p>
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input
            v-model="adjustForm.reason"
            type="textarea"
            :rows="2"
            maxlength="100"
            show-word-limit
            placeholder="例如：采购入库 / 盘点修正"
          />
        </el-form-item>
      </el-form>

      <div class="target-stock" :class="{ invalid: targetStock < 0 }">
        <span>调整后库存</span>
        <strong>{{ targetStock }}</strong>
      </div>

      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" :loading="adjustSubmitting" @click="submitAdjust">
          确认调整
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="logsVisible"
      :title="`库存流水 · ${logSku?.productName ?? ''}`"
      width="680px"
    >
      <el-table v-loading="logsLoading" :data="logs" row-key="id" max-height="420">
        <el-table-column label="时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.createTime, true) }}</template>
        </el-table-column>
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ typeLabels[row.type] || row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="变动" width="90" align="right">
          <template #default="{ row }">
            <span :class="row.changeNum > 0 ? 'text-success' : 'text-danger'">
              {{ row.changeNum > 0 ? '+' : '' }}{{ row.changeNum }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="关联订单">
          <template #default="{ row }">{{ row.orderNo || '-' }}</template>
        </el-table-column>
      </el-table>

      <AppEmpty v-if="!logsLoading && logs.length === 0" description="暂无库存流水" />

      <el-pagination
        v-if="logTotal > 0"
        :current-page="logPage"
        :page-size="logSize"
        :page-sizes="[10, 20, 50]"
        :total="logTotal"
        small
        background
        layout="total, sizes, prev, pager, next"
        @current-change="handleLogPageChange"
        @size-change="handleLogSizeChange"
      />
    </el-dialog>
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

.page-alert {
  margin-bottom: 16px;
}

.table-wrap {
  padding: 8px 8px 16px;
  overflow: hidden;
}

.product-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.product-cell strong {
  font-size: 14px;
  font-weight: 600;
  color: var(--coffee-900);
}

.product-cell span {
  font-size: 11px;
  color: var(--muted);
}

.legend {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: auto;
  font-size: 11px;
  color: var(--muted);
}

.legend span {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}

.dot.danger {
  background: var(--el-color-danger);
}

.dot.warning {
  background: var(--el-color-warning);
}

.dot.success {
  background: var(--el-color-success);
}

.adjust-summary {
  display: flex;
  flex-direction: column;
  gap: 3px;
  margin-bottom: 18px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #faf5ee;
}

.adjust-summary strong {
  font-size: 14px;
  color: var(--coffee-900);
}

.adjust-summary span {
  font-size: 12px;
  color: var(--muted);
}

.form-hint {
  width: 100%;
  margin-top: 6px;
  font-size: 11px;
  color: var(--muted);
}

.target-stock {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f2f5ec;
  color: var(--coffee-700);
  font-size: 13px;
}

.target-stock strong {
  font-size: 22px;
  color: var(--coffee-900);
  font-variant-numeric: tabular-nums;
}

.target-stock.invalid {
  background: #faeceb;
  color: var(--el-color-danger);
}

.target-stock.invalid strong {
  color: var(--el-color-danger);
}

@media (max-width: 900px) {
  .legend {
    width: 100%;
    margin-left: 0;
    margin-top: 4px;
  }
}
</style>
