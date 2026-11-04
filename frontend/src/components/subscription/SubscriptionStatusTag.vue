<script setup lang="ts">
import { computed } from 'vue'

import { subscriptionStatusMeta } from '@/utils/format'

const props = defineProps<{ status?: string | null }>()

const meta = computed(() => subscriptionStatusMeta(props.status))
</script>

<template>
  <el-tag :type="meta.type" effect="light" round>{{ meta.label }}</el-tag>
</template>
