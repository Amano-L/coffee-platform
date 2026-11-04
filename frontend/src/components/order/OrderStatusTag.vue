<script setup lang="ts">
import { computed } from 'vue'

import { orderStatusMeta } from '@/utils/format'

const props = defineProps<{ status?: string | null }>()

const meta = computed(() => orderStatusMeta(props.status))
</script>

<template>
  <el-tag :type="meta.type" effect="light" round>{{ meta.label }}</el-tag>
</template>
