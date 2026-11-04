<script setup lang="ts">
withDefaults(
  defineProps<{
    eyebrow?: string
    title: string
    subtitle?: string
  }>(),
  {
    eyebrow: '',
    subtitle: '',
  },
)
</script>

<template>
  <div class="section-head">
    <div>
      <span v-if="eyebrow" class="eyebrow">{{ eyebrow }}</span>
      <h2 class="section-title">{{ title }}</h2>
      <p v-if="subtitle" class="section-subtitle">{{ subtitle }}</p>
    </div>
    <div class="extra">
      <slot name="extra" />
    </div>
  </div>
</template>

<style scoped>
.extra {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
