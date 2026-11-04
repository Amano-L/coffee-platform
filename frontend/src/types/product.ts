import type { PageQuery } from './api'

export type RoastLevel = 'LIGHT' | 'MEDIUM' | 'DARK'
export type ProcessMethod = 'WASHED' | 'NATURAL' | 'HONEY'
export type GrindType = 'WHOLE_BEAN' | 'GROUND'

/**
 * 风味标签在文档中为 string[]，后端列表 VO 当前返回逗号分隔字符串。
 * 前端统一兼容两种形态，展示前用 format.parseFlavorTags 归一化。
 */
export type FlavorTags = string | string[] | null | undefined

export interface ProductSummary {
  id: number
  name: string
  origin?: string | null
  roastLevel?: string | null
  flavorTags?: FlavorTags
  processMethod?: string | null
  minPrice: number
  coverUrl?: string | null
}

export interface Sku {
  id: number
  weight: string
  grindType: string
  price: number
  stock: number
}

export interface ProductDetail {
  id: number
  name: string
  origin?: string | null
  roastLevel?: string | null
  flavorTags?: string[] | null
  processMethod?: string | null
  description?: string | null
  coverUrl?: string | null
  skus: Sku[]
}

export interface ProductQuery extends PageQuery {
  keyword?: string
  roastLevel?: string
  flavorTag?: string
  processMethod?: string
}
