<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

import AppEmpty from '@/components/common/AppEmpty.vue'
import {
  createAdminProduct,
  deleteAdminProduct,
  getAdminProductDetail,
  getAdminProducts,
  updateAdminProduct,
  updateAdminProductStatus,
} from '@/api/admin'
import { usePagination } from '@/composables/usePagination'
import type { AdminProduct, AdminProductPayload } from '@/types/admin'
import {
  GRIND_TYPE_OPTIONS,
  PROCESS_METHOD_OPTIONS,
  PRODUCT_STATUS_META,
  ROAST_LEVEL_OPTIONS,
} from '@/utils/constants'
import {
  flavorTagsToString,
  formatDateTime,
  formatPrice,
  parseFlavorTags,
  roastLevelLabel,
} from '@/utils/format'

interface SkuForm {
  /** 已有 SKU 的 ID；新增行为 undefined */
  id?: number
  /** 前端表格稳定 key，不提交给后端 */
  _key: string
  weight: string
  grindType: string
  price: number
  stock: number
}

let skuKeySeed = 0
const nextSkuKey = (): string => `sku-${++skuKeySeed}`

interface ProductForm {
  name: string
  origin: string
  roastLevel: string
  flavorTags: string
  processMethod: string
  description: string
  coverUrl: string
  skus: SkuForm[]
}

const createEmptyForm = (): ProductForm => ({
  name: '',
  origin: '',
  roastLevel: 'LIGHT',
  flavorTags: '',
  processMethod: 'WASHED',
  description: '',
  coverUrl: '',
  skus: [{ _key: nextSkuKey(), weight: '250g', grindType: 'WHOLE_BEAN', price: 68, stock: 100 }],
})

const products = ref<AdminProduct[]>([])
const loading = ref(false)
const loadError = ref('')
const keyword = ref('')
const roastLevel = ref('')
const statusFilter = ref('')

const { page, size, total, applyPageResult, handlePageChange, handleSizeChange } =
  usePagination(10)

const dialogVisible = ref(false)
const dialogLoading = ref(false)
const saving = ref(false)
const editingId = ref<number | null>(null)
const statusUpdatingId = ref<number | null>(null)
const deletingId = ref<number | null>(null)

const productFormRef = ref<FormInstance>()
const productForm = ref<ProductForm>(createEmptyForm())

const rules: FormRules<ProductForm> = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { max: 128, message: '商品名称不能超过 128 个字符', trigger: 'blur' },
  ],
}

const isEdit = computed(() => editingId.value !== null)
const dialogTitle = computed(() => (isEdit.value ? '编辑商品' : '新增商品'))

function productStatusMeta(status: number): { label: string; type: 'success' | 'info' } {
  const meta = PRODUCT_STATUS_META[status]
  if (meta) return { label: meta.label, type: meta.type === 'success' ? 'success' : 'info' }
  return { label: `状态 ${status}`, type: 'info' }
}

function displayPrice(value?: number | null): string {
  return value === undefined || value === null ? '-' : formatPrice(value)
}

function displayTags(tags?: string | string[] | null): string {
  const list = parseFlavorTags(tags)
  return list.length > 0 ? list.join('、') : '-'
}

async function fetchProducts(): Promise<void> {
  loading.value = true
  loadError.value = ''
  try {
    const result = await getAdminProducts({
      page: page.value,
      size: size.value,
      keyword: keyword.value.trim() || undefined,
      roastLevel: roastLevel.value || undefined,
      status: statusFilter.value ? Number(statusFilter.value) : undefined,
    })
    products.value = result.records ?? []
    applyPageResult(result)
  } catch (error) {
    products.value = []
    loadError.value = error instanceof Error ? error.message : '请求失败'
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  page.value = 1
  void fetchProducts()
}

function handleReset(): void {
  keyword.value = ''
  roastLevel.value = ''
  statusFilter.value = ''
  page.value = 1
  void fetchProducts()
}

function handleCurrentChange(value: number): void {
  handlePageChange(value)
  void fetchProducts()
}

function handleSizeChangeClick(value: number): void {
  handleSizeChange(value)
  void fetchProducts()
}

function openCreate(): void {
  editingId.value = null
  Object.assign(productForm.value, createEmptyForm())
  dialogVisible.value = true
}

async function openEdit(row: AdminProduct): Promise<void> {
  editingId.value = row.id
  Object.assign(productForm.value, createEmptyForm())
  dialogVisible.value = true
  dialogLoading.value = true
  try {
    const detail = await getAdminProductDetail(row.id)
    productForm.value.name = detail.name
    productForm.value.origin = detail.origin ?? ''
    productForm.value.roastLevel = detail.roastLevel ?? 'LIGHT'
    productForm.value.processMethod = detail.processMethod ?? 'WASHED'
    productForm.value.flavorTags = flavorTagsToString(detail.flavorTags)
    productForm.value.description = detail.description ?? ''
    productForm.value.coverUrl = detail.coverUrl ?? ''
    productForm.value.skus = (detail.skus ?? []).map((sku) => ({
      id: sku.id,
      _key: nextSkuKey(),
      weight: sku.weight,
      grindType: sku.grindType,
      price: Number(sku.price),
      stock: sku.stock,
    }))
    if (productForm.value.skus.length === 0) {
      productForm.value.skus = [
        { _key: nextSkuKey(), weight: '250g', grindType: 'WHOLE_BEAN', price: 0, stock: 0 },
      ]
    }
  } catch {
    // 详情失败时不允许编辑，避免用空字段覆盖原有数据
    dialogVisible.value = false
    ElMessage.warning('商品详情加载失败，暂时无法编辑该商品')
  } finally {
    dialogLoading.value = false
  }
}

function addSku(): void {
  productForm.value.skus.push({
    _key: nextSkuKey(),
    weight: '',
    grindType: 'WHOLE_BEAN',
    price: 0,
    stock: 0,
  })
}

function removeSku(index: number): void {
  const sku = productForm.value.skus[index]
  if (!sku) return
  if (sku.id) {
    ElMessage.warning('已有 SKU 不支持删除，可修改规格、价格或库存')
    return
  }
  if (productForm.value.skus.length <= 1) {
    ElMessage.warning('至少保留一个 SKU')
    return
  }
  productForm.value.skus.splice(index, 1)
}

function validateSkus(): boolean {
  if (productForm.value.skus.length === 0) {
    ElMessage.warning('请至少添加一个 SKU')
    return false
  }
  const combinations = new Set<string>()
  for (const [index, sku] of productForm.value.skus.entries()) {
    if (!sku.weight.trim()) {
      ElMessage.warning(`第 ${index + 1} 个 SKU 请填写重量规格`)
      return false
    }
    if (!Number.isFinite(Number(sku.price)) || Number(sku.price) < 0) {
      ElMessage.warning(`第 ${index + 1} 个 SKU 价格不合法`)
      return false
    }
    if (!Number.isInteger(Number(sku.stock)) || Number(sku.stock) < 0) {
      ElMessage.warning(`第 ${index + 1} 个 SKU 库存必须是非负整数`)
      return false
    }
    const key = `${sku.weight.trim()}__${sku.grindType}`
    if (combinations.has(key)) {
      ElMessage.warning(`第 ${index + 1} 个 SKU 与前面的重量/研磨方式重复`)
      return false
    }
    combinations.add(key)
  }
  return true
}

async function handleSubmit(): Promise<void> {
  if (!productFormRef.value) return
  try {
    await productFormRef.value.validate()
  } catch {
    return
  }
  if (!validateSkus()) return

  const payload: AdminProductPayload = {
    name: productForm.value.name.trim(),
    origin: productForm.value.origin.trim(),
    roastLevel: productForm.value.roastLevel,
    flavorTags: productForm.value.flavorTags.trim(),
    processMethod: productForm.value.processMethod,
    description: productForm.value.description.trim(),
    coverUrl: productForm.value.coverUrl.trim(),
    skus: productForm.value.skus.map((sku) => ({
      id: sku.id,
      weight: sku.weight.trim(),
      grindType: sku.grindType,
      price: Number(sku.price),
      stock: Number(sku.stock),
    })),
  }

  saving.value = true
  try {
    if (editingId.value) {
      await updateAdminProduct(editingId.value, payload)
      ElMessage.success('商品与 SKU 已更新')
    } else {
      await createAdminProduct(payload)
      ElMessage.success('商品已创建')
    }
    dialogVisible.value = false
    void fetchProducts()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    saving.value = false
  }
}

async function handleToggleStatus(row: AdminProduct): Promise<void> {
  const nextStatus = row.status === 1 ? 0 : 1
  const action = nextStatus === 1 ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(`确定${action}「${row.name}」吗？`, `${action}商品`, {
      confirmButtonText: action,
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  statusUpdatingId.value = row.id
  try {
    await updateAdminProductStatus(row.id, nextStatus)
    ElMessage.success(`商品已${action}`)
    void fetchProducts()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    statusUpdatingId.value = null
  }
}

async function handleDelete(row: AdminProduct): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `确定删除「${row.name}」吗？删除后商品与 SKU 将下架，历史订单快照不受影响。`,
      '删除商品',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' },
    )
  } catch {
    return
  }
  deletingId.value = row.id
  try {
    await deleteAdminProduct(row.id)
    ElMessage.success('商品已删除')
    if (products.value.length === 1 && page.value > 1) {
      page.value -= 1
    }
    void fetchProducts()
  } catch {
    // 错误提示已由 Axios 拦截器统一处理
  } finally {
    deletingId.value = null
  }
}

onMounted(() => {
  void fetchProducts()
})
</script>

<template>
  <div class="admin-page">
    <header class="admin-page-head">
      <div>
        <span class="eyebrow">CATALOG</span>
        <h1>商品管理</h1>
        <p>维护咖啡豆信息、SKU、上下架与删除。</p>
      </div>
      <div class="head-actions">
        <el-button plain :loading="loading" @click="fetchProducts">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button type="primary" @click="openCreate">
          <el-icon><Plus /></el-icon>
          新增商品
        </el-button>
      </div>
    </header>

    <el-alert
      v-if="loadError"
      type="error"
      :closable="false"
      show-icon
      title="后台商品列表加载失败"
      :description="loadError"
      class="page-alert"
    >
      <template #default>
        <el-button size="small" type="primary" plain @click="fetchProducts">重新加载</el-button>
      </template>
    </el-alert>

    <el-form class="filter-bar" :inline="true" @submit.prevent="handleSearch">
      <el-form-item label="关键词">
        <el-input
          v-model="keyword"
          placeholder="搜索商品名称/产区"
          clearable
          style="width: 210px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="烘焙度">
        <el-select v-model="roastLevel" placeholder="全部" clearable style="width: 130px">
          <el-option
            v-for="option in ROAST_LEVEL_OPTIONS"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="statusFilter" placeholder="全部" clearable style="width: 120px">
          <el-option label="上架中" value="1" />
          <el-option label="已下架" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <section class="surface table-wrap">
      <el-table v-loading="loading" :data="products" row-key="id">
        <el-table-column prop="id" label="ID" width="72" />
        <el-table-column label="商品" min-width="220">
          <template #default="{ row }">
            <div class="product-cell">
              <strong>{{ row.name }}</strong>
              <span>{{ row.origin || '未标注产区' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="烘焙度" width="100">
          <template #default="{ row }">{{ roastLevelLabel(row.roastLevel) }}</template>
        </el-table-column>
        <el-table-column label="风味" min-width="170">
          <template #default="{ row }">
            <span class="tags-text">{{ displayTags(row.flavorTags) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="价格" width="110" align="right">
          <template #default="{ row }">{{ displayPrice(row.minPrice) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="104">
          <template #default="{ row }">
            <el-tag :type="productStatusMeta(row.status).type" effect="light" round>
              {{ productStatusMeta(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="openEdit(row as AdminProduct)">编辑</el-button>
            <el-button
              text
              :loading="statusUpdatingId === row.id"
              @click="handleToggleStatus(row as AdminProduct)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button
              text
              type="danger"
              :loading="deletingId === row.id"
              @click="handleDelete(row as AdminProduct)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <AppEmpty
        v-if="!loading && products.length === 0 && !loadError"
        description="没有符合条件的商品"
        action-text="新增商品"
        show-action
        @action="openCreate"
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="760px"
      :close-on-click-modal="false"
    >
      <div v-loading="dialogLoading">
        <el-alert
          v-if="isEdit"
          type="info"
          :closable="false"
          show-icon
          title="保存后会同步更新 SKU 列表；已有 SKU 不支持删除，可修改规格、价格与库存。"
          class="dialog-alert"
        />

        <el-form ref="productFormRef" :model="productForm" :rules="rules" label-position="top">
          <div class="form-grid two">
            <el-form-item label="商品名称" prop="name">
              <el-input v-model="productForm.name" maxlength="128" placeholder="例如：埃塞俄比亚 耶加雪菲" />
            </el-form-item>
            <el-form-item label="产区">
              <el-input v-model="productForm.origin" maxlength="64" placeholder="例如：埃塞俄比亚" />
            </el-form-item>
          </div>

          <div class="form-grid two">
            <el-form-item label="烘焙度">
              <el-select v-model="productForm.roastLevel" style="width: 100%">
                <el-option
                  v-for="option in ROAST_LEVEL_OPTIONS"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="处理法">
              <el-select v-model="productForm.processMethod" style="width: 100%">
                <el-option
                  v-for="option in PROCESS_METHOD_OPTIONS"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
          </div>

          <div class="form-grid two">
            <el-form-item label="风味标签">
              <el-input v-model="productForm.flavorTags" placeholder="逗号分隔，例如：花香,柑橘" />
            </el-form-item>
            <el-form-item label="封面图地址">
              <el-input v-model="productForm.coverUrl" maxlength="255" placeholder="https://..." />
            </el-form-item>
          </div>

          <el-form-item label="商品描述">
            <el-input
              v-model="productForm.description"
              type="textarea"
              :rows="3"
              placeholder="描述咖啡豆的风味、产地故事等"
            />
          </el-form-item>

          <div class="sku-editor">
            <div class="sku-head">
              <span>SKU 列表</span>
              <el-button text type="primary" @click="addSku">
                <el-icon><Plus /></el-icon>
                添加 SKU
              </el-button>
            </div>
            <div class="sku-head-row">
              <span>重量规格</span>
              <span>研磨方式</span>
              <span>价格（元）</span>
              <span>库存</span>
              <span />
            </div>
            <div v-for="(sku, index) in productForm.skus" :key="sku._key" class="sku-row">
              <el-input v-model="sku.weight" placeholder="250g" />
              <el-select v-model="sku.grindType">
                <el-option
                  v-for="option in GRIND_TYPE_OPTIONS"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
              <el-input-number v-model="sku.price" :min="0" :precision="2" :step="1" />
              <el-input-number v-model="sku.stock" :min="0" :step="1" />
              <el-button
                text
                type="danger"
                :disabled="Boolean(sku.id) || productForm.skus.length <= 1"
                :title="sku.id ? '已有 SKU 不支持删除' : '删除 SKU'"
                @click="removeSku(index)"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <p class="sku-hint">
              {{ isEdit ? '带 ID 的 SKU 会更新原记录；新增行会创建新 SKU。' : '创建商品时至少需要一个 SKU。' }}
            </p>
          </div>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '创建商品' }}
        </el-button>
      </template>
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

.head-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.page-alert {
  margin-bottom: 16px;
}

.alert-text {
  margin: 4px 0 10px;
  font-size: 12px;
  line-height: 1.7;
}

.alert-text code {
  padding: 1px 6px;
  border-radius: 4px;
  background: rgba(62, 39, 35, 0.08);
  font-size: 12px;
}

.alert-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
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
  color: var(--coffee-900);
  font-weight: 600;
}

.product-cell span {
  font-size: 11px;
  color: var(--muted);
}

.tags-text {
  font-size: 12px;
  color: var(--coffee-600);
}

.dialog-alert {
  margin-bottom: 16px;
}

.sku-hint {
  margin-top: 10px;
  font-size: 11px;
  line-height: 1.6;
  color: var(--muted);
}

.form-grid {
  display: grid;
  gap: 14px;
}

.form-grid.two {
  grid-template-columns: 1fr 1fr;
}

.sku-editor {
  margin-top: 6px;
  padding: 16px;
  border: 1px solid var(--line-soft);
  border-radius: 12px;
  background: #fbf6ef;
}

.sku-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--coffee-800);
}

.sku-head-row,
.sku-row {
  display: grid;
  grid-template-columns: 1.2fr 1fr 1.1fr 1fr 40px;
  gap: 10px;
  align-items: center;
}

.sku-head-row {
  margin-bottom: 6px;
  font-size: 11px;
  color: var(--muted);
}

.sku-row {
  margin-bottom: 10px;
}

.sku-row:last-child {
  margin-bottom: 0;
}

.sku-row :deep(.el-input-number) {
  width: 100%;
}

.sku-row :deep(.el-select) {
  width: 100%;
}

.sku-readonly {
  margin-top: 6px;
  padding: 16px;
  border: 1px solid var(--line-soft);
  border-radius: 12px;
  background: #fbf6ef;
}

.sku-readonly-title {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 600;
  color: var(--coffee-800);
}

@media (max-width: 720px) {
  .form-grid.two {
    grid-template-columns: 1fr;
  }

  .sku-head-row {
    display: none;
  }

  .sku-row {
    grid-template-columns: 1fr 1fr;
  }

  .sku-row :deep(.el-button) {
    grid-column: 1 / -1;
    justify-self: flex-start;
  }
}
</style>
