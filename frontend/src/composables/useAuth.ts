import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { useUserStore } from '@/stores/user'

/** 登录态与登录跳转的轻量封装 */
export function useAuth() {
  const userStore = useUserStore()
  const router = useRouter()
  const route = useRoute()

  const isLoggedIn = computed(() => userStore.isLoggedIn)
  const isAdmin = computed(() => userStore.isAdmin)
  const displayName = computed(() => userStore.displayName)

  function redirectToLogin(message = '请先登录后再操作'): void {
    ElMessage.warning(message)
    router.push({ path: '/login', query: { redirect: route.fullPath } })
  }

  /** 需要登录的操作前调用，返回是否已登录 */
  function ensureLogin(): boolean {
    if (!userStore.isLoggedIn) {
      redirectToLogin()
      return false
    }
    return true
  }

  return {
    userStore,
    isLoggedIn,
    isAdmin,
    displayName,
    ensureLogin,
    redirectToLogin,
    router,
    route,
  }
}
