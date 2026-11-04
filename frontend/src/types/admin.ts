import type { PageQuery } from './api'
import type { Sku } from './product'

export interface DashboardStats {
  totalSales: number
  orderCount: number
  subscriptionCount: number
  activeSubscriptionCount: number
}

/** GET /admin/products 结构，包含下架商品 */
export interface AdminProduct {
  id: number
  name: string
  origin?: string | null
  roastLevel?: string | null
  flavorTags?: string | string[] | null
  processMethod?: string | null
  minPrice?: number
  coverUrl?: string | null
  status: number
  createTime?: string | null
}

export interface AdminProductQuery extends PageQuery {
  keyword?: string
  roastLevel?: string
  status?: number
}

/** GET /admin/products/{id} 结构，包含下架商品与 SKU */
export interface AdminProductDetail {
  id: number
  name: string
  origin?: string | null
  roastLevel?: string | null
  flavorTags?: string[] | null
  processMethod?: string | null
  description?: string | null
  coverUrl?: string | null
  status: number
  createTime?: string | null
  skus: Sku[]
}

export interface AdminSkuPayload {
  /** 已有 SKU 的 ID；为空表示新增 */
  id?: number
  weight: string
  grindType: string
  price: number
  stock: number
}

export interface AdminProductPayload {
  name: string
  origin?: string
  roastLevel?: string
  /** 后端入参为逗号分隔字符串 */
  flavorTags?: string
  processMethod?: string
  description?: string
  coverUrl?: string
  skus?: AdminSkuPayload[]
}

export interface AdminOrder {
  orderNo: string
  userId: number
  payAmount: number
  status: string
  createTime: string
}

export interface AdminSubscription {
  id: number
  userId: number
  username?: string | null
  planName: string
  period?: string | null
  status: string
  nextDeliveryDate: string
  createTime?: string | null
}

export interface AdminSubscriptionQuery extends PageQuery {
  status?: string
}
