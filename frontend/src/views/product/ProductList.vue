<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'

import AppEmpty from '@/components/common/AppEmpty.vue'
import ProductCard from '@/components/product/ProductCard.vue'
import { getProductList } from '@/api/product'
import { usePagination } from '@/composables/usePagination'
import type { ProductSummary } from '@/types/product'
import { FLAVOR_TAG_SUGGESTIONS, PROCESS_METHOD_OPTIONS, ROAST_LEVEL_OPTIONS } from '@/utils/constants'

const filters = reactive({
  keyword: '',
  roastLevel: '',
  processMethod: '',
  flavorTag: '',
})

const products = ref<ProductSummary[]>([])
const loading = ref(false)
const pageSizes = [12, 24, 48]

const { page, size, total, applyPageResult, handlePageChange, handleSizeChange } =
  usePagination(12)

async function fetchProducts(): Promise<void> {
  loading.value = true
  try {
    const result = await getProductList({
      page: page.value,
      size: size.value,
      keyword: filters.keyword.trim() || undefined,
      roastLevel: filters.roastLevel || undefined,
      processMethod: filters.processMethod || undefined,
      flavorTag: filters.flavorTag || undefined,
    })
    products.value = result.records ?? []
    applyPageResult(result)
  } catch {
    products.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch(): void {
  page.value = 1
  void fetchProducts()
}

function handleReset(): void {
  filters.keyword = ''
  filters.roastLevel = ''
  filters.processMethod = ''
  filters.flavorTag = ''
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

onMounted(() => {
  void fetchProducts()
})
</script>

<template>
  <div class="page">
    <div class="page-container">
      <header class="page-head">
        <div>
          <span class="eyebrow">OUR COFFEE</span>
          <h1 class="page-title">咖啡豆</h1>
          <p class="page-subtitle">按烘焙度、处理法与风味标签，找到今天想喝的那一杯。</p>
        </div>
        <div class="head-note">
          <el-icon><Sunny /></el-icon>
          当季新鲜烘焙
        </div>
      </header>

      <el-form class="filter-bar" :inline="true" @submit.prevent="handleSearch">
        <el-form-item label="关键词">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索咖啡豆名称"
            clearable
            style="width: 200px"
            :prefix-icon="Search"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="烘焙度">
          <el-select v-model="filters.roastLevel" placeholder="全部" clearable style="width: 130px">
            <el-option
              v-for="option in ROAST_LEVEL_OPTIONS"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="处理法">
          <el-select
            v-model="filters.processMethod"
            placeholder="全部"
            clearable
            style="width: 130px"
          >
            <el-option
              v-for="option in PROCESS_METHOD_OPTIONS"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="风味">
          <el-select
            v-model="filters.flavorTag"
            placeholder="选择或输入"
            clearable
            filterable
            allow-create
            default-first-option
            style="width: 150px"
          >
            <el-option v-for="tag in FLAVOR_TAG_SUGGESTIONS" :key="tag" :label="tag" :value="tag" />
          </el-select>
        </el-form-item>
        <el-form-item class="filter-actions">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="result-meta">
        <span>
          共 <strong>{{ total }}</strong> 款咖啡
        </span>
        <span v-if="filters.keyword" class="keyword">“{{ filters.keyword }}” 的搜索结果</span>
      </div>

      <div v-if="loading" class="product-grid">
        <div v-for="n in 8" :key="n" class="skeleton-card">
          <el-skeleton animated>
            <template #template>
              <el-skeleton-item variant="image" style="width: 100%; height: 210px" />
              <div style="padding: 16px">
                <el-skeleton-item variant="text" style="width: 38%" />
                <el-skeleton-item variant="h3" style="width: 82%; margin-top: 10px" />
                <el-skeleton-item variant="text" style="width: 52%; margin-top: 12px" />
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
        description="没有找到符合条件的咖啡豆，换个筛选条件试试吧"
        action-text="重置筛选"
        show-action
        @action="handleReset"
      />

      <el-pagination
        v-if="total > 0"
        :current-page="page"
        :page-size="size"
        :page-sizes="pageSizes"
        :total="total"
        background
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChangeClick"
      />
    </div>
  </div>
</template>

<style scoped>
.head-note {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 999px;
  background: #f2e7da;
  color: var(--caramel-dark);
  font-size: 12px;
  letter-spacing: 0.04em;
}

.filter-actions {
  margin-left: auto;
}

.filter-actions :deep(.el-form-item__content) {
  gap: 8px;
}

.result-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 18px;
  font-size: 13px;
  color: var(--muted);
}

.result-meta strong {
  color: var(--coffee-900);
}

.keyword {
  padding: 3px 10px;
  border-radius: 999px;
  background: #f4ebe1;
  color: var(--coffee-700);
}

.skeleton-card {
  background: var(--paper);
  border: 1px solid var(--line-soft);
  border-radius: 18px;
  overflow: hidden;
}

@media (max-width: 768px) {
  .filter-actions {
    margin-left: 0;
  }

  .filter-bar :deep(.el-form-item) {
    width: 100%;
    margin-right: 0;
  }

  .filter-bar :deep(.el-input),
  .filter-bar :deep(.el-select) {
    width: 100% !important;
  }

  .result-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
}
</style>
