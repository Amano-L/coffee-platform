import type { PageResult } from '@/types/api'
import type {
  AdjustStockPayload,
  InventoryLog,
  InventoryLogQuery,
  InventoryQuery,
  InventorySku,
} from '@/types/inventory'
import request from '@/utils/request'

/** 库存 SKU 列表 GET /inventory/skus */
export function getInventorySkus(params: InventoryQuery): Promise<PageResult<InventorySku>> {
  return request.get<PageResult<InventorySku>>('/inventory/skus', { params })
}

/** 查询单个 SKU 库存 GET /inventory/skus/{skuId} */
export function getSkuStock(skuId: number): Promise<Partial<InventorySku>> {
  return request.get<Partial<InventorySku>>(`/inventory/skus/${skuId}`)
}

/** 调整库存 PUT /inventory/skus/{skuId} */
export function adjustStock(
  skuId: number,
  data: AdjustStockPayload,
): Promise<Partial<InventorySku>> {
  return request.put<Partial<InventorySku>>(`/inventory/skus/${skuId}`, data)
}

/** 库存流水 GET /inventory/logs */
export function getInventoryLogs(params: InventoryLogQuery): Promise<PageResult<InventoryLog>> {
  return request.get<PageResult<InventoryLog>>('/inventory/logs', { params })
}
