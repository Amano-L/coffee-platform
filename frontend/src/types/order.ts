import type { PageQuery } from './api'

/** 后端订单状态为中文：待支付/已支付/待发货/已发货/已完成/已取消 */
export type OrderStatus = string

export interface OrderSummary {
  orderNo: string
  totalAmount: number
  payAmount: number
  status: OrderStatus
  createTime: string
}

export interface OrderItem {
  skuId: number
  productName: string
  price: number
  quantity: number
}

export interface OrderDetail {
  orderNo: string
  totalAmount: number
  payAmount: number
  status: OrderStatus
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  items: OrderItem[]
  createTime: string
}

export interface OrderCreatePayload {
  addressId: number
  cartItemIds: number[]
  couponId?: number | null
}

export interface OrderCreateResult {
  orderNo: string
  totalAmount: number
  payAmount: number
  status: OrderStatus
}

export interface OrderQuery extends PageQuery {
  status?: string
}
