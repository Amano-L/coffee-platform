<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import NotificationCenter from '@/components/common/NotificationCenter.vue'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const mobileMenuOpen = ref(false)

const year = computed(() => new Date().getFullYear())

const navLinks = [
  { path: '/', label: '首页' },
  { path: '/products', label: '咖啡豆' },
  { path: '/subscriptions/plans', label: '订阅计划' },
]

function isActive(path: string): boolean {
  if (path === '/') return route.path === '/'
  if (path === '/products') return route.path.startsWith('/products')
  return route.path.startsWith('/subscriptions/plans')
}

async function handleCommand(command: string): Promise<void> {
  switch (command) {
    case 'profile':
      await router.push('/user/profile')
      break
    case 'address':
      await router.push('/user/address')
      break
    case 'preference':
      await router.push('/user/preference')
      break
    case 'orders':
      await router.push('/orders')
      break
    case 'subscriptions':
      await router.push('/subscriptions/my')
      break
    case 'admin':
      await router.push('/admin')
      break
    case 'logout': {
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
      cartStore.reset()
      ElMessage.success('已退出登录')
      await router.push('/login')
      break
    }
  }
}

function loadCart(): void {
  if (!userStore.isLoggedIn) return
  void cartStore.fetchCart().catch(() => undefined)
}

onMounted(loadCart)

watch(
  () => userStore.token,
  (token) => {
    if (token) loadCart()
    else cartStore.reset()
  },
)

// 路由变化时自动收起移动端菜单
watch(
  () => route.path,
  () => {
    mobileMenuOpen.value = false
  },
)
</script>

<template>
  <div class="site">
    <header class="site-header">
      <div class="page-container header-inner">
        <router-link to="/" class="brand" aria-label="拾豆咖啡首页">
          <span class="brand-mark">
            <el-icon :size="20"><Coffee /></el-icon>
          </span>
          <span class="brand-text">
            <strong>拾豆咖啡</strong>
            <small>BEAN PICK</small>
          </span>
        </router-link>

        <nav class="main-nav" aria-label="主导航">
          <router-link
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            class="nav-link"
            :class="{ active: isActive(link.path) }"
          >
            {{ link.label }}
          </router-link>
        </nav>

        <div class="header-actions">
          <el-button
            class="mobile-menu-btn"
            circle
            text
            aria-label="打开菜单"
            @click="mobileMenuOpen = true"
          >
            <el-icon :size="19"><Menu /></el-icon>
          </el-button>

          <NotificationCenter v-if="userStore.isLoggedIn" />

          <router-link to="/cart" class="cart-link" aria-label="购物车">
            <el-badge :value="cartStore.totalQuantity" :hidden="cartStore.totalQuantity === 0" :max="99">
              <el-icon :size="19"><ShoppingCart /></el-icon>
            </el-badge>
            <span class="cart-text">购物车</span>
          </router-link>

          <el-dropdown v-if="userStore.isLoggedIn" trigger="click" @command="handleCommand">
            <button type="button" class="user-trigger">
              <el-avatar :size="30" :src="userStore.avatarUrl || undefined">
                {{ userStore.displayName.slice(0, 1) }}
              </el-avatar>
              <span class="user-name">{{ userStore.displayName }}</span>
              <el-icon class="caret"><ArrowDown /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="address">
                  <el-icon><Location /></el-icon>收货地址
                </el-dropdown-item>
                <el-dropdown-item command="preference">
                  <el-icon><Coffee /></el-icon>咖啡偏好
                </el-dropdown-item>
                <el-dropdown-item command="orders" divided>
                  <el-icon><Tickets /></el-icon>我的订单
                </el-dropdown-item>
                <el-dropdown-item command="subscriptions">
                  <el-icon><Refresh /></el-icon>我的订阅
                </el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" command="admin" divided>
                  <el-icon><Setting /></el-icon>后台管理
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <div v-else class="auth-links">
            <router-link to="/login" class="login-link">登录</router-link>
            <router-link to="/register" class="register-btn">注册</router-link>
          </div>
        </div>
      </div>
    </header>

    <main class="site-main">
      <router-view />
    </main>

    <footer class="site-footer">
      <div class="page-container footer-inner">
        <div class="footer-brand">
          <span class="brand-mark light">
            <el-icon :size="18"><Coffee /></el-icon>
          </span>
          <div>
            <strong>拾豆咖啡</strong>
            <p>从一颗豆开始，喝到世界的风味。</p>
          </div>
        </div>
        <div class="footer-links">
          <router-link to="/products">全部咖啡豆</router-link>
          <router-link to="/subscriptions/plans">订阅计划</router-link>
          <router-link to="/orders">我的订单</router-link>
          <router-link to="/user/preference">咖啡偏好</router-link>
        </div>
        <div class="footer-meta">
          <span>© {{ year }} Bean Pick Coffee</span>
          <span>精选 · 新鲜 · 可持续</span>
        </div>
      </div>
    </footer>

    <el-drawer
      v-model="mobileMenuOpen"
      direction="rtl"
      size="290px"
      :with-header="false"
      class="mobile-drawer"
    >
      <div class="drawer-inner">
        <div class="drawer-head">
          <span class="brand-mark">
            <el-icon :size="19"><Coffee /></el-icon>
          </span>
          <div>
            <strong>拾豆咖啡</strong>
            <small>BEAN PICK</small>
          </div>
        </div>

        <nav class="drawer-nav" aria-label="移动端导航">
          <router-link v-for="link in navLinks" :key="link.path" :to="link.path" class="drawer-link">
            {{ link.label }}
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </nav>

        <template v-if="userStore.isLoggedIn">
          <div class="drawer-divider">我的账户</div>
          <nav class="drawer-nav">
            <router-link to="/user/profile" class="drawer-link">个人中心 <el-icon><User /></el-icon></router-link>
            <router-link to="/user/address" class="drawer-link">收货地址 <el-icon><Location /></el-icon></router-link>
            <router-link to="/user/preference" class="drawer-link">咖啡偏好 <el-icon><Coffee /></el-icon></router-link>
            <router-link to="/orders" class="drawer-link">我的订单 <el-icon><Tickets /></el-icon></router-link>
            <router-link to="/subscriptions/my" class="drawer-link">我的订阅 <el-icon><Refresh /></el-icon></router-link>
            <router-link to="/notifications" class="drawer-link">消息通知 <el-icon><Bell /></el-icon></router-link>
            <router-link v-if="userStore.isAdmin" to="/admin" class="drawer-link">后台管理 <el-icon><Setting /></el-icon></router-link>
          </nav>
          <el-button class="drawer-logout" plain @click="handleCommand('logout')">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </template>
        <template v-else>
          <div class="drawer-auth">
            <el-button type="primary" @click="router.push('/login')">登录</el-button>
            <el-button plain @click="router.push('/register')">注册</el-button>
          </div>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.site {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.site-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(248, 243, 236, 0.9);
  backdrop-filter: blur(14px);
  border-bottom: 1px solid rgba(232, 222, 210, 0.8);
}

.header-inner {
  display: flex;
  align-items: center;
  gap: 28px;
  height: 72px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: none;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: var(--coffee-900);
  color: #f7f1ea;
  box-shadow: 0 6px 14px rgba(62, 39, 35, 0.22);
}

.brand-mark.light {
  background: rgba(255, 250, 244, 0.14);
  box-shadow: none;
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.15;
}

.brand-text strong {
  font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
  font-size: 18px;
  letter-spacing: 0.06em;
  color: var(--coffee-900);
}

.brand-text small {
  font-size: 9px;
  letter-spacing: 0.3em;
  color: var(--caramel-dark);
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.nav-link {
  position: relative;
  padding: 8px 14px;
  font-size: 14px;
  color: var(--coffee-600);
  border-radius: 8px;
  transition:
    color 0.2s ease,
    background 0.2s ease;
}

.nav-link:hover {
  color: var(--coffee-900);
  background: rgba(232, 222, 210, 0.45);
}

.nav-link.active {
  color: var(--coffee-900);
  font-weight: 600;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  left: 14px;
  right: 14px;
  bottom: 2px;
  height: 2px;
  border-radius: 2px;
  background: var(--caramel);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: none;
}

.mobile-menu-btn {
  display: none;
  color: var(--coffee-700);
}

.mobile-menu-btn:hover {
  background: #f2e8dd;
}

.drawer-inner {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  padding: 6px 4px;
}

.drawer-head {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 18px;
  border-bottom: 1px solid var(--line-soft);
}

.drawer-head strong {
  display: block;
  font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
  font-size: 17px;
  color: var(--coffee-900);
  letter-spacing: 0.05em;
}

.drawer-head small {
  font-size: 9px;
  letter-spacing: 0.26em;
  color: var(--caramel-dark);
}

.drawer-nav {
  display: flex;
  flex-direction: column;
  padding: 12px 0;
}

.drawer-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 10px;
  border-radius: 10px;
  font-size: 15px;
  color: var(--coffee-800);
  transition: background 0.2s ease;
}

.drawer-link:hover {
  background: #f7efe6;
  color: var(--coffee-950);
}

.drawer-link .el-icon {
  color: #b3a294;
  font-size: 14px;
}

.drawer-divider {
  margin: 6px 10px 2px;
  padding-top: 14px;
  border-top: 1px solid var(--line-soft);
  font-size: 11px;
  letter-spacing: 0.14em;
  color: var(--caramel-dark);
}

.drawer-logout {
  margin-top: auto;
  width: 100%;
}

.drawer-auth {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.drawer-auth .el-button {
  flex: 1;
}

.cart-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 10px;
  color: var(--coffee-700);
  font-size: 13px;
  transition: background 0.2s ease;
}

.cart-link:hover {
  background: #f2e8dd;
}

.cart-link :deep(.el-badge__content) {
  background: var(--caramel-dark);
  border: none;
}

.user-trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px 4px 4px;
  border: 1px solid transparent;
  border-radius: 999px;
  background: transparent;
  cursor: pointer;
  color: var(--coffee-800);
  transition: all 0.2s ease;
}

.user-trigger:hover {
  background: #f4ebe1;
  border-color: var(--line);
}

.user-name {
  max-width: 110px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
}

.caret {
  font-size: 12px;
  color: var(--muted);
}

.auth-links {
  display: flex;
  align-items: center;
  gap: 10px;
}

.login-link {
  padding: 8px 12px;
  font-size: 14px;
  color: var(--coffee-700);
}

.register-btn {
  padding: 8px 18px;
  border-radius: 999px;
  background: var(--coffee-900);
  color: #fffaf4;
  font-size: 13px;
  transition: background 0.2s ease;
}

.register-btn:hover {
  background: var(--caramel-dark);
}

.site-main {
  flex: 1;
}

.site-footer {
  background: var(--coffee-950);
  color: #d9c8bb;
  padding: 44px 0 28px;
  margin-top: 20px;
}

.footer-inner {
  display: grid;
  grid-template-columns: 1.4fr 1fr 1fr;
  gap: 36px;
  align-items: start;
}

.footer-brand {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.footer-brand strong {
  color: #fffaf4;
  font-family: 'Noto Serif SC', 'Songti SC', Georgia, serif;
  font-size: 17px;
  letter-spacing: 0.05em;
}

.footer-brand p {
  margin-top: 4px;
  font-size: 13px;
  color: #a89284;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px 24px;
  font-size: 13px;
}

.footer-links a {
  color: #c3b0a3;
  transition: color 0.2s ease;
}

.footer-links a:hover {
  color: #fffaf4;
}

.footer-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-end;
  font-size: 12px;
  color: #8e7a6d;
}

@media (max-width: 900px) {
  .mobile-menu-btn {
    display: inline-flex;
  }

  .header-inner {
    gap: 14px;
    height: 66px;
  }

  .main-nav {
    display: none;
  }

  .header-actions {
    margin-left: auto;
  }

  .brand-text small {
    display: none;
  }

  .cart-text,
  .user-name {
    display: none;
  }

  .footer-inner {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .footer-meta {
    align-items: flex-start;
  }
}

@media (max-width: 520px) {
  .brand-text strong {
    font-size: 16px;
  }

  .login-link {
    display: none;
  }

  .header-actions {
    gap: 2px;
  }
}
</style>
