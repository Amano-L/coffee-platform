import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { login as loginApi, logout as logoutApi } from '@/api/auth'
import { getCurrentUser, updateProfile as updateProfileApi } from '@/api/user'
import type { LoginPayload, LoginResult, UpdateProfilePayload, UserInfo, UserProfile } from '@/types/user'
import { clearAuth, getStoredUserInfo, getToken, setAuth, setStoredUserInfo } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref<UserInfo | null>(getStoredUserInfo())
  const profile = ref<UserProfile | null>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => Boolean(token.value))
  // 后端登录响应 UserInfoVO.role 为 USER / ADMIN
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const displayName = computed(
    () => userInfo.value?.nickname || userInfo.value?.username || '咖啡爱好者',
  )
  const avatarUrl = computed(() => userInfo.value?.avatarUrl || profile.value?.avatarUrl || '')

  async function login(payload: LoginPayload): Promise<LoginResult> {
    loading.value = true
    try {
      const result = await loginApi(payload)
      token.value = result.token
      userInfo.value = result.userInfo
      profile.value = null
      setAuth(result.token, result.userInfo)
      return result
    } finally {
      loading.value = false
    }
  }

  /** 获取完整资料；/users/me 不含 role，因此保留登录时写入的角色 */
  async function fetchUserInfo(force = false): Promise<UserProfile | null> {
    if (!token.value) return null
    if (profile.value && !force) return profile.value
    const data = await getCurrentUser()
    profile.value = data
    const role = userInfo.value?.role ?? null
    userInfo.value = {
      id: data.id,
      username: data.username,
      nickname: data.nickname,
      avatarUrl: data.avatarUrl,
      role,
    }
    setStoredUserInfo(userInfo.value)
    return data
  }

  async function updateUserInfo(payload: UpdateProfilePayload): Promise<UserProfile> {
    const data = await updateProfileApi(payload)
    profile.value = data
    const role = userInfo.value?.role ?? null
    userInfo.value = {
      id: data.id,
      username: data.username,
      nickname: data.nickname,
      avatarUrl: data.avatarUrl,
      role,
    }
    setStoredUserInfo(userInfo.value)
    return data
  }

  function reset(): void {
    token.value = ''
    userInfo.value = null
    profile.value = null
    clearAuth()
  }

  async function logout(): Promise<void> {
    try {
      if (token.value) await logoutApi()
    } catch {
      // Token 已失效时忽略服务端异常，继续清理本地状态
    } finally {
      reset()
    }
  }

  return {
    token,
    userInfo,
    profile,
    loading,
    isLoggedIn,
    isAdmin,
    displayName,
    avatarUrl,
    login,
    logout,
    fetchUserInfo,
    updateUserInfo,
    reset,
  }
})
