export type SubscriptionStatus = 'ACTIVE' | 'PAUSED' | 'CANCELLED' | string

export interface SubscriptionPlan {
  id: number
  name: string
  period: string
  price: number
  description?: string | null
}

export interface UserSubscription {
  id: number
  planName: string
  period: string
  status: SubscriptionStatus
  nextDeliveryDate: string
  createTime: string
}

export interface CreateSubscriptionPayload {
  planId: number
  roastLevel?: string
  flavorTags?: string[]
  processMethod?: string
}

export interface CreateSubscriptionResult {
  /** 兼容后端 UserSubscriptionVO 的 id 字段 */
  id?: number
  subscriptionId: number
  planName: string
  status: SubscriptionStatus
  nextDeliveryDate: string
  period?: string
  createTime?: string
}
