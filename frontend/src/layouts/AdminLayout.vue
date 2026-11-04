<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import type { Component } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  Box,
  Coffee,
  DataBoard,
  Expand,
  Fold,
  Goods,
  HomeFilled,
  Refresh,
  SwitchButton,
  Tickets,
} from '@element-plus/icons-vue'

import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const isNarrow = ref(false)
const collapsed = computed(() => appStore.sidebarCollapsed || isNarrow.value)

const breadcrumbs = computed(() =>
  route.matched
    .filter((record) => Boolean(record.meta.title))
    .map((record) => ({ path: record.path, title: record.meta.title as string })),
)

const menuItems: { path: string; label: string; icon: Component }[] = [
  { path: '/admin', label: '数据看板', icon: DataBoard },
  { path: '/admin/products', label: '商品管理', icon: Goods },
  { path: '/admin/inventory', label: '库存管理', icon: Box },
  { path: '/admin/orders', label: '订单管理', icon: Tickets },
  { path: '/admin/subscriptions', label: '订阅管理', icon: Refresh },
]

function handleResize(): void {
  isNarrow.value = window.innerWidth < 900
}

onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})

async function handleCommand(command: string): Promise<void> {
  if (command === 'front') {
    await router.push('/')
    return
  }
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出当前账号吗？', '退出登录', {
        confirmButtonText: '退出',
        cancelButtonText: '取消',
        type: 'warning',
      })
    } catch {
      return
    }
    await userStore.logout()
    ElMessage.success('已退出登录')
    await router.push('/login')
  }
}
</script>

<template>
  <div class="admin-shell">
    <aside class="admin-aside" :class="{ collapsed }">
      <router-link to="/admin" class="admin-brand">
        <span class="brand-mark">
          <el-icon :size="18"><Coffee /></el-icon>
        </span>
        <span v-if="!collapsed" class="brand-text">
          <strong>拾豆咖啡</strong>
          <small>管理后台</small>
        </span>
      </router-link>

      <el-menu
        :default-active="route.path"
        :collapse="collapsed"
        :collapse-transition="false"
        router
        class="admin-menu"
      >
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon>
            <component :is="item.icon" />
          </el-icon>
          <template #title>{{ item.label }}</template>
        </el-menu-item>
      </el-menu>

      <div class="aside-footer">
        <el-button
          v-if="!isNarrow"
          text
          class="collapse-btn"
          :aria-label="collapsed ? '展开侧边栏' : '收起侧边栏'"
          @click="appStore.toggleSidebar()"
        >
          <el-icon><component :is="collapsed ? Expand : Fold" /></el-icon>
          <span v-if="!collapsed">收起菜单</span>
        </el-button>
      </div>
    </aside>

    <div class="admin-body">
      <header class="admin-header">
        <div class="header-left">
          <span class="mobile-page-title">{{ route.meta.title }}</span>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">前台首页</el-breadcrumb-item>
            <el-breadcrumb-item
              v-for="(item, index) in breadcrumbs"
              :key="`${item.path}-${item.title}`"
              :to="index < breadcrumbs.length - 1 ? { path: item.path } : undefined"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <router-link to="/" class="front-link">
            <el-icon><HomeFilled /></el-icon>
            <span>回到前台</span>
          </router-link>
          <el-dropdown trigger="click" @command="handleCommand">
            <button type="button" class="admin-user">
              <el-avatar :size="28" :src="userStore.avatarUrl || undefined">
                {{ userStore.displayName.slice(0, 1) }}
              </el-avatar>
              <span>{{ userStore.displayName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="front">
                  <el-icon><HomeFilled /></el-icon>回到前台
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-shell {
  display: flex;
  min-height: 100vh;
  background: #f6f1ea;
}

.admin-aside {
  display: flex;
  flex-direction: column;
  width: 220px;
  flex: none;
  background: var(--coffee-950);
  color: #d9c8bb;
  transition: width 0.22s ease;
  position: sticky;
  top: 0;
  height: 100vh;
  z-index: 20;
}

.admin-aside.collapsed {
  width: 68px;
}

.admin-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 72px;
  padding: 0 16px;
  flex: none;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  flex: none;
  border-radius: 10px;
  background: rgba(255, 250, 244, 0.12);
  color: #fffaf4;
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
}

.brand-text strong {
  color: #fffaf4;
  font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
  font-size: 16px;
  letter-spacing: 0.05em;
}

.brand-text small {
  font-size: 10px;
  letter-spacing: 0.18em;
  color: #a89284;
}

.admin-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 12px 8px;
  --el-menu-bg-color: transparent;
  --el-menu-text-color: #c3b0a3;
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.08);
  --el-menu-active-color: #fffaf4;
  --el-menu-item-height: 48px;
}

.admin-menu :deep(.el-menu-item) {
  border-radius: 10px;
  margin-bottom: 4px;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: var(--caramel-dark);
  color: #fffaf4;
}

.admin-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08);
}

.aside-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.collapse-btn {
  width: 100%;
  color: #a89284;
  justify-content: flex-start;
}

.collapse-btn:hover {
  color: #fffaf4;
  background: rgba(255, 255, 255, 0.08);
}

.admin-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.admin-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  height: 64px;
  padding: 0 26px;
  background: rgba(255, 253, 249, 0.92);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--line-soft);
}

.header-left {
  min-width: 0;
  overflow: hidden;
}

.mobile-page-title {
  display: none;
  font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
  font-size: 16px;
  color: var(--coffee-900);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
  flex: none;
}

.front-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--coffee-600);
}

.front-link:hover {
  color: var(--coffee-900);
}

.admin-user {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 3px 8px 3px 3px;
  border: 1px solid transparent;
  border-radius: 999px;
  background: transparent;
  cursor: pointer;
  color: var(--coffee-800);
  font-size: 13px;
}

.admin-user:hover {
  background: #f4ebe1;
  border-color: var(--line);
}

.admin-content {
  flex: 1;
  padding: 26px;
  min-width: 0;
}

@media (max-width: 900px) {
  .admin-aside {
    width: 68px;
  }

  .admin-header {
    padding: 0 16px;
  }

  .admin-content {
    padding: 18px 14px;
  }

  .header-left :deep(.el-breadcrumb) {
    display: none;
  }

  .mobile-page-title {
    display: inline-block;
    padding-left: 10px;
  }

  .header-right {
    margin-left: auto;
  }

  .front-link span {
    display: none;
  }
}
</style>
