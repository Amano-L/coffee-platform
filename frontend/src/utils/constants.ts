export type TagType = 'primary' | 'success' | 'warning' | 'info' | 'danger'

export interface SelectOption {
  label: string
  value: string
}

export const DEFAULT_PAGE_SIZE = 10
export const PAGE_SIZES = [10, 20, 50, 100]

export const ROAST_LEVEL_OPTIONS: SelectOption[] = [
  { label: '浅烘', value: 'LIGHT' },
  { label: '中烘', value: 'MEDIUM' },
  { label: '深烘', value: 'DARK' },
]

export const PROCESS_METHOD_OPTIONS: SelectOption[] = [
  { label: '水洗', value: 'WASHED' },
  { label: '日晒', value: 'NATURAL' },
  { label: '蜜处理', value: 'HONEY' },
]

export const GRIND_TYPE_OPTIONS: SelectOption[] = [
  { label: '整豆', value: 'WHOLE_BEAN' },
  { label: '研磨粉', value: 'GROUND' },
]

export const PERIOD_OPTIONS: SelectOption[] = [
  { label: '每周', value: 'WEEKLY' },
  { label: '双周', value: 'BIWEEKLY' },
  { label: '每月', value: 'MONTHLY' },
]

export const ORDER_STATUS_OPTIONS: SelectOption[] = [
  { label: '待支付', value: '待支付' },
  { label: '已支付', value: '已支付' },
  { label: '待发货', value: '待发货' },
  { label: '已发货', value: '已发货' },
  { label: '已完成', value: '已完成' },
  { label: '已取消', value: '已取消' },
]

export const FLAVOR_TAG_SUGGESTIONS = [
  '花香',
  '柑橘',
  '茉莉',
  '莓果',
  '坚果',
  '巧克力',
  '焦糖',
  '蜂蜜',
  '热带水果',
]

const toLabelMap = (options: SelectOption[]): Record<string, string> =>
  options.reduce<Record<string, string>>((map, item) => {
    map[item.value] = item.label
    return map
  }, {})

export const ROAST_LEVEL_LABELS = toLabelMap(ROAST_LEVEL_OPTIONS)
export const PROCESS_METHOD_LABELS = toLabelMap(PROCESS_METHOD_OPTIONS)
export const GRIND_TYPE_LABELS = toLabelMap(GRIND_TYPE_OPTIONS)
export const PERIOD_LABELS = toLabelMap(PERIOD_OPTIONS)

export const ORDER_STATUS_META: Record<string, { label: string; type: TagType }> = {
  待支付: { label: '待支付', type: 'warning' },
  已支付: { label: '已支付', type: 'primary' },
  待发货: { label: '待发货', type: 'warning' },
  已发货: { label: '已发货', type: 'primary' },
  已完成: { label: '已完成', type: 'success' },
  已取消: { label: '已取消', type: 'info' },
}

export const SUBSCRIPTION_STATUS_META: Record<string, { label: string; type: TagType }> = {
  ACTIVE: { label: '生效中', type: 'success' },
  PAUSED: { label: '已暂停', type: 'warning' },
  CANCELLED: { label: '已取消', type: 'info' },
}

export const PAYMENT_STATUS_META: Record<string, { label: string; type: TagType }> = {
  待支付: { label: '待支付', type: 'warning' },
  已支付: { label: '已支付', type: 'success' },
  已退款: { label: '已退款', type: 'info' },
}

export const PRODUCT_STATUS_META: Record<number, { label: string; type: TagType }> = {
  1: { label: '上架中', type: 'success' },
  0: { label: '已下架', type: 'info' },
}

export const ORDER_STATUS_STEPS = ['待支付', '已支付', '已发货', '已完成']
