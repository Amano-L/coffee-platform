import axios from 'axios'
import type { AxiosError, AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

import type { ApiResponse } from '@/types/api'
import { clearAuth, getToken } from '@/utils/auth'

declare module 'axios' {
  export interface AxiosRequestConfig {
    /** 静默请求：失败时不弹出全局错误提示 */
    silent?: boolean
  }
}

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
})

/** 登录失效：清理本地凭证并回到登录页（整页跳转可重置所有 Store） */
function handleUnauthorized(): void {
  clearAuth()
  const { pathname, search, hash } = window.location
  if (pathname === '/login' || pathname === '/register') return
  const redirect = encodeURIComponent(`${pathname}${search}${hash}`)
  window.location.assign(`/login?redirect=${redirect}`)
}

function notifyError(message: string, silent?: boolean): void {
  if (!silent) ElMessage.error(message)
}

function httpStatusMessage(status?: number): string {
  switch (status) {
    case 400:
      return '请求参数有误'
    case 401:
      return '登录已失效，请重新登录'
    case 403:
      return '没有权限执行该操作'
    case 404:
      return '请求的资源不存在'
    case 405:
      return '接口不支持当前请求方式'
    case 422:
      return '请求参数校验失败'
    case 500:
      return '服务器开小差了，请稍后再试'
    default:
      return '网络异常，请稍后再试'
  }
}

// 请求拦截：注入 Token
service.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：统一解包 code / data，处理 401 与业务错误
service.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResponse<unknown> | undefined
    if (body && typeof body === 'object' && typeof body.code === 'number') {
      if (body.code === 0) {
        return body.data as unknown as typeof response
      }
      if (body.code === 401) {
        notifyError(body.message || '登录已失效，请重新登录', response.config.silent)
        handleUnauthorized()
        return Promise.reject(new Error(body.message || '登录已失效')) as unknown as typeof response
      }
      const message = body.message || '请求失败'
      notifyError(message, response.config.silent)
      return Promise.reject(new Error(message)) as unknown as typeof response
    }
    return response.data as unknown as typeof response
  },
  (error: AxiosError<ApiResponse<unknown>>) => {
    const silent = error.config?.silent
    const status = error.response?.status
    const body = error.response?.data

    if (status === 401 || body?.code === 401) {
      notifyError(body?.message || '登录已失效，请重新登录', silent)
      handleUnauthorized()
      return Promise.reject(new Error(body?.message || '登录已失效'))
    }

    const message = body?.message || httpStatusMessage(status)
    notifyError(message, silent)
    return Promise.reject(new Error(message))
  },
)

/**
 * 统一请求入口，已解包为业务数据。
 * 组件与 Store 不直接使用 axios，只通过 api/ 模块调用。
 */
const request = {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.get(url, config) as unknown as Promise<T>
  },
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, config) as unknown as Promise<T>
  },
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return service.put(url, data, config) as unknown as Promise<T>
  },
  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return service.delete(url, config) as unknown as Promise<T>
  },
}

export default request
