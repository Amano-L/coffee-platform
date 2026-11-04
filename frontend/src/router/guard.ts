import type { Router } from 'vue-router'
import { ElMessage } from 'element-plus'

import { useUserStore } from '@/stores/user'

const APP_TITLE = import.meta.env.VITE_APP_TITLE || '拾豆咖啡'

/** 登录拦截 + 角色路由守卫 */
export function setupRouterGuards(router: Router): void {
  router.beforeEach((to) => {
    const userStore = useUserStore()

    document.title = to.meta.title ? `${to.meta.title} · ${APP_TITLE}` : APP_TITLE

    // 已登录用户不再进入登录/注册页
    if ((to.name === 'login' || to.name === 'register') && userStore.isLoggedIn) {
      return { path: '/' }
    }

    if (to.meta.requiresAuth && !userStore.isLoggedIn) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }

    if (to.meta.requiresAdmin && !userStore.isAdmin) {
      ElMessage.warning('该页面仅管理员可访问')
      return { path: '/' }
    }

    return true
  })
}
