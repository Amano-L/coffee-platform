import type { PageResult } from '@/types/api'
import type { ProductDetail, ProductQuery, ProductSummary } from '@/types/product'
import request from '@/utils/request'

/** 商品列表 GET /products */
export function getProductList(params: ProductQuery): Promise<PageResult<ProductSummary>> {
  return request.get<PageResult<ProductSummary>>('/products', { params })
}

/** 商品详情 GET /products/{id} */
export function getProductDetail(id: number): Promise<ProductDetail> {
  return request.get<ProductDetail>(`/products/${id}`)
}
