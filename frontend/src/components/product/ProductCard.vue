<script setup lang="ts">
import { computed } from 'vue'

import ProductImage from '@/components/common/ProductImage.vue'
import PriceText from '@/components/common/PriceText.vue'
import type { ProductSummary } from '@/types/product'
import { parseFlavorTags, processMethodLabel, roastLevelLabel } from '@/utils/format'

const props = defineProps<{ product: ProductSummary }>()

const tags = computed(() => parseFlavorTags(props.product.flavorTags).slice(0, 3))
</script>

<template>
  <router-link :to="`/products/${product.id}`" class="product-card">
    <ProductImage :src="product.coverUrl" :alt="product.name" />
    <div class="body">
      <div class="meta">
        <span>{{ roastLevelLabel(product.roastLevel) }}</span>
        <span v-if="product.origin" class="dot">·</span>
        <span v-if="product.origin" class="origin">{{ product.origin }}</span>
      </div>
      <h3 class="name line-clamp-2">{{ product.name }}</h3>
      <div class="tags">
        <span v-for="tag in tags" :key="tag" class="tag">{{ tag }}</span>
        <span v-if="product.processMethod" class="tag tag-process">
          {{ processMethodLabel(product.processMethod) }}
        </span>
      </div>
      <div class="foot">
        <PriceText :value="product.minPrice" />
        <span class="go">查看详情<el-icon><ArrowRight /></el-icon></span>
      </div>
    </div>
  </router-link>
</template>

<style scoped>
.product-card {
  display: flex;
  flex-direction: column;
  background: var(--paper);
  border: 1px solid var(--line-soft);
  border-radius: 18px;
  overflow: hidden;
  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease,
    border-color 0.25s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 38px rgba(62, 39, 35, 0.12);
  border-color: var(--line);
}

.product-card:hover :deep(.product-image img) {
  transform: scale(1.04);
}

.body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 18px 18px 20px;
  flex: 1;
}

.meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--caramel-dark);
  letter-spacing: 0.04em;
}

.dot {
  opacity: 0.5;
}

.origin {
  color: var(--muted);
}

.name {
  font-size: 17px;
  line-height: 1.45;
  color: var(--coffee-900);
  min-height: 49px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  min-height: 24px;
}

.tag {
  padding: 2px 9px;
  border-radius: 999px;
  background: #f6ede2;
  color: #7a5a42;
  font-size: 11px;
  line-height: 18px;
}

.tag-process {
  background: #eef1e8;
  color: #5f6d4e;
}

.foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px dashed var(--line-soft);
}

.foot :deep(.price) {
  font-size: 19px;
}

.go {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: var(--coffee-500);
}
</style>
