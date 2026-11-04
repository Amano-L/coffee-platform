import type { LoginPayload, LoginResult, RegisterPayload, RegisterResult } from '@/types/user'
import request from '@/utils/request'

/** 用户注册 POST /auth/register */
export function register(data: RegisterPayload): Promise<RegisterResult> {
  return request.post<RegisterResult>('/auth/register', data)
}

/** 用户登录 POST /auth/login */
export function login(data: LoginPayload): Promise<LoginResult> {
  return request.post<LoginResult>('/auth/login', data)
}

/** 登出 POST /auth/logout */
export function logout(): Promise<null> {
  return request.post<null>('/auth/logout')
}
