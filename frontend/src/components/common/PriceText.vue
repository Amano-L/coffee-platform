<script setup lang="ts">
import { computed } from 'vue'

import { formatMoney } from '@/utils/format'

const props = withDefaults(
  defineProps<{
    value?: number | string | null
    size?: 'sm' | 'base' | 'lg'
    suffix?: string
  }>(),
  {
    value: 0,
    size: 'base',
    suffix: '',
  },
)

const sizeClass = computed(() => (props.size === 'base' ? '' : `price-${props.size}`))
</script>

<template>
  <span class="price" :class="sizeClass">
    <span class="currency">¥</span>{{ formatMoney(value) }}<span v-if="suffix" class="suffix">{{ suffix }}</span>
  </span>
</template>

<style scoped>
.currency {
  font-size: 0.72em;
  margin-right: 1px;
  font-weight: 600;
}

.suffix {
  font-size: 0.55em;
  font-weight: 400;
  color: var(--muted);
  margin-left: 4px;
}
</style>
