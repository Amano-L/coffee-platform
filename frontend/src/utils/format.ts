import {
  GRIND_TYPE_LABELS,
  ORDER_STATUS_META,
  PERIOD_LABELS,
  PROCESS_METHOD_LABELS,
  ROAST_LEVEL_LABELS,
  SUBSCRIPTION_STATUS_META,
} from './constants'
import type { TagType } from './constants'

/** 金额格式化：136 -> "136.00" */
export function formatMoney(value?: number | string | null): string {
  const num = Number(value ?? 0)
  return Number.isFinite(num) ? num.toFixed(2) : '0.00'
}

/** 带符号金额：136 -> "¥136.00" */
export function formatPrice(value?: number | string | null): string {
  return `¥${formatMoney(value)}`
}

/** 日期时间：兼容 "2026-10-14 10:00:00" 与 ISO 字符串 */
export function formatDateTime(value?: string | null, withSeconds = false): string {
  if (!value) return '-'
  const normalized = value.replace('T', ' ').replace(/\.\d+Z?$/, '')
  const limit = withSeconds ? 19 : 16
  return normalized.length > limit ? normalized.slice(0, limit) : normalized
}

/** 日期：兼容 LocalDate 与时间字符串 */
export function formatDate(value?: string | null): string {
  if (!value) return '-'
  return formatDateTime(value).slice(0, 10)
}

/** 风味标签兼容 string[] 与逗号分隔字符串两种后端形态 */
export function parseFlavorTags(value?: string | string[] | null): string[] {
  if (!value) return []
  if (Array.isArray(value)) {
    return value.map((item) => String(item).trim()).filter(Boolean)
  }
  return value
    .split(/[,，、]/)
    .map((item) => item.trim())
    .filter(Boolean)
}

/** 后台表单需要逗号分隔字符串 */
export function flavorTagsToString(tags?: string[] | null): string {
  return (tags ?? []).join(',')
}

export function roastLevelLabel(value?: string | null): string {
  if (!value) return '未标注'
  return ROAST_LEVEL_LABELS[value] ?? value
}

export function processMethodLabel(value?: string | null): string {
  if (!value) return '未标注'
  return PROCESS_METHOD_LABELS[value] ?? value
}

export function grindTypeLabel(value?: string | null): string {
  if (!value) return '未标注'
  return GRIND_TYPE_LABELS[value] ?? value
}

export function periodLabel(value?: string | null): string {
  if (!value) return '-'
  return PERIOD_LABELS[value] ?? value
}

export function orderStatusMeta(status?: string | null): { label: string; type: TagType } {
  if (!status) return { label: '未知状态', type: 'info' }
  return ORDER_STATUS_META[status] ?? { label: status, type: 'info' }
}

export function subscriptionStatusMeta(status?: string | null): {
  label: string
  type: TagType
} {
  if (!status) return { label: '未知状态', type: 'info' }
  return SUBSCRIPTION_STATUS_META[status] ?? { label: status, type: 'info' }
}
