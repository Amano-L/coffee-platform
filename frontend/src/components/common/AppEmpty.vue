<script setup lang="ts">
import emptyImage from '@/assets/images/empty-coffee.svg'

withDefaults(
  defineProps<{
    description?: string
    actionText?: string
    showAction?: boolean
  }>(),
  {
    description: '这里还没有内容',
    actionText: '去看看',
    showAction: false,
  },
)

const emit = defineEmits<{ action: [] }>()
</script>

<template>
  <div class="empty-block">
    <el-empty :image="emptyImage" :image-size="96" :description="description">
      <el-button v-if="showAction" type="primary" plain @click="emit('action')">
        {{ actionText }}
      </el-button>
    </el-empty>
  </div>
</template>
