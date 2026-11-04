import type { PageResult } from '@/types/api'
import type {
  OrderCreatePayload,
  OrderCreateResult,
  OrderDetail,
  OrderQuery,
  OrderSummary,
} from '@/types/order'
import request from '@/utils/request'

/** 创建订单 POST /orders */
export function createOrder(data: OrderCreatePayload): Promise<OrderCreateResult> {
  return request.post<OrderCreateResult>('/orders', data)
}

/** 订单列表 GET /orders */
export function getOrderList(params: OrderQuery): Promise<PageResult<OrderSummary>> {
  return request.get<PageResult<OrderSummary>>('/orders', { params })
}

/** 订单详情 GET /orders/{orderNo} */
export function getOrderDetail(orderNo: string): Promise<OrderDetail> {
  return request.get<OrderDetail>(`/orders/${orderNo}`)
}

/** 取消订单 POST /orders/{orderNo}/cancel */
export function cancelOrder(orderNo: string): Promise<null> {
  return request.post<null>(`/orders/${orderNo}/cancel`)
}
