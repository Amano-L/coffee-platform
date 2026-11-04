<script setup lang="ts">
import { Bell, Coffee, Location, Refresh, Tickets, User } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const links = [
  { path: '/user/profile', label: '个人中心', icon: User },
  { path: '/user/address', label: '收货地址', icon: Location },
  { path: '/user/preference', label: '咖啡偏好', icon: Coffee },
  { path: '/orders', label: '我的订单', icon: Tickets },
  { path: '/subscriptions/my', label: '我的订阅', icon: Refresh },
  { path: '/notifications', label: '消息通知', icon: Bell },
]

function isActive(path: string): boolean {
  if (path === '/orders') return route.path.startsWith('/orders')
  if (path === '/subscriptions/my') return route.path.startsWith('/subscriptions')
  return route.path === path
}
</script>

<template>
  <nav class="user-nav" aria-label="个人中心导航">
    <router-link
      v-for="link in links"
      :key="link.path"
      :to="link.path"
      class="nav-item"
      :class="{ active: isActive(link.path) }"
    >
      <el-icon><component :is="link.icon" /></el-icon>
      <span>{{ link.label }}</span>
    </router-link>
  </nav>
</template>

<style scoped>
.user-nav {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px;
  background: var(--paper);
  border: 1px solid var(--line-soft);
  border-radius: 14px;
  margin-bottom: 26px;
}

.nav-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 16px;
  border-radius: 10px;
  font-size: 14px;
  color: var(--coffee-600);
  transition: all 0.2s ease;
}

.nav-item:hover {
  background: #f7efe6;
  color: var(--coffee-900);
}

.nav-item.active {
  background: var(--coffee-900);
  color: #fffaf4;
}

@media (max-width: 560px) {
  .nav-item {
    padding: 8px 12px;
    font-size: 13px;
  }
}
</style>
