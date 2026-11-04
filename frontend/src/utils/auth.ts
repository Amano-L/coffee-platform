import type { UserInfo } from '@/types/user'

const TOKEN_KEY = 'coffee_platform_token'
const USER_KEY = 'coffee_platform_user'

export function getToken(): string {
  return localStorage.getItem(TOKEN_KEY) ?? ''
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function getStoredUserInfo(): UserInfo | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as UserInfo
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export function setStoredUserInfo(userInfo: UserInfo | null): void {
  if (userInfo) {
    localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
  } else {
    localStorage.removeItem(USER_KEY)
  }
}

export function setAuth(token: string, userInfo: UserInfo | null): void {
  setToken(token)
  setStoredUserInfo(userInfo)
}

export function clearAuth(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}
