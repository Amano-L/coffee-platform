import type { PageQuery, PageResult } from '@/types/api'
import type {
  AdminOrder,
  AdminProduct,
  AdminProductDetail,
  AdminProductPayload,
  AdminProductQuery,
  AdminSubscription,
  AdminSubscriptionQuery,
  DashboardStats,
} from '@/types/admin'
import request from '@/utils/request'

/** 数据看板 GET /admin/dashboard */
export function getDashboard(): Promise<DashboardStats> {
  return request.get<DashboardStats>('/admin/dashboard')
}

/** 后台商品列表 GET /admin/products（包含下架商品） */
export function getAdminProducts(
  params: AdminProductQuery,
  options?: { silent?: boolean },
): Promise<PageResult<AdminProduct>> {
  return request.get<PageResult<AdminProduct>>('/admin/products', {
    params,
    silent: options?.silent,
  })
}

/** 后台商品详情 GET /admin/products/{id}（包含下架商品与 SKU） */
export function getAdminProductDetail(id: number): Promise<AdminProductDetail> {
  return request.get<AdminProductDetail>(`/admin/products/${id}`)
}

/** 新增商品 POST /admin/products */
export function createAdminProduct(data: AdminProductPayload): Promise<unknown> {
  return request.post<unknown>('/admin/products', data)
}

/** 修改商品 PUT /admin/products/{id}（可同时更新/新增 SKU） */
export function updateAdminProduct(id: number, data: AdminProductPayload): Promise<unknown> {
  return request.put<unknown>(`/admin/products/${id}`, data)
}

/** 上下架商品 PUT /admin/products/{id}/status */
export function updateAdminProductStatus(id: number, status: number): Promise<unknown> {
  return request.put<unknown>(`/admin/products/${id}/status`, { status })
}

/** 删除商品 DELETE /admin/products/{id}（逻辑删除） */
export function deleteAdminProduct(id: number): Promise<null> {
  return request.delete<null>(`/admin/products/${id}`)
}

/** 后台订单列表 GET /admin/orders */
export function getAdminOrders(params: PageQuery): Promise<PageResult<AdminOrder>> {
  return request.get<PageResult<AdminOrder>>('/admin/orders', { params })
}

/** 订单发货 PUT /admin/orders/{orderNo}/ship */
export function shipAdminOrder(orderNo: string): Promise<unknown> {
  return request.put<unknown>(`/admin/orders/${orderNo}/ship`)
}

/** 后台订阅列表 GET /admin/subscriptions */
export function getAdminSubscriptions(
  params: AdminSubscriptionQuery,
  options?: { silent?: boolean },
): Promise<PageResult<AdminSubscription>> {
  return request.get<PageResult<AdminSubscription>>('/admin/subscriptions', {
    params,
    silent: options?.silent,
  })
}

/** 后台暂停订阅 PUT /admin/subscriptions/{id}/pause */
export function pauseAdminSubscription(id: number): Promise<null> {
  return request.put<null>(`/admin/subscriptions/${id}/pause`)
}

/** 后台取消订阅 PUT /admin/subscriptions/{id}/cancel */
export function cancelAdminSubscription(id: number): Promise<null> {
  return request.put<null>(`/admin/subscriptions/${id}/cancel`)
}
