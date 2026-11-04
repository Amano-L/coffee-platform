<script setup lang="ts">
import { ref, watch } from 'vue'

import placeholder from '@/assets/images/product-placeholder.svg'

const props = withDefaults(
  defineProps<{
    src?: string | null
    alt?: string
    ratio?: string
  }>(),
  {
    src: '',
    alt: '咖啡商品',
    ratio: '1 / 1',
  },
)

const failed = ref(false)

watch(
  () => props.src,
  () => {
    failed.value = false
  },
)
</script>

<template>
  <div class="product-image" :style="{ aspectRatio: ratio }">
    <img v-if="src && !failed" :src="src" :alt="alt" loading="lazy" @error="failed = true" />
    <img v-else :src="placeholder" :alt="alt" loading="lazy" />
  </div>
</template>

<style scoped>
.product-image {
  width: 100%;
  overflow: hidden;
  background: var(--cream-deep);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}
</style>
