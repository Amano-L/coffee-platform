import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    /** 需要登录 */
    requiresAuth?: boolean
    /** 需要管理员角色 */
    requiresAdmin?: boolean
    /** 浏览器标题 */
    title?: string
  }
}

export {}
