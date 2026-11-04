/**
 * 通用响应与分页结构，严格对应《05-API设计文档》1.3 / 1.4。
 */
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  traceId?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

export interface PageQuery {
  page?: number
  size?: number
}
