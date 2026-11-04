import type { PageQuery } from './api'

export interface InventorySku {
  skuId: number
  productId: number
  productName: string
  origin?: string | null
  weight: string
  grindType: string
  price: number
  stock: number
  version: number
}

export interface InventoryLog {
  id: number
  skuId: number
  orderNo?: string | null
  changeNum: number
  type: string
  createTime: string
}

export interface InventoryQuery extends PageQuery {
  keyword?: string
}

export interface InventoryLogQuery extends PageQuery {
  skuId?: number
  orderNo?: string
}

export interface AdjustStockPayload {
  changeNum: number
  reason: string
}
