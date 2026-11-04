import type {
  CreateSubscriptionPayload,
  CreateSubscriptionResult,
  SubscriptionPlan,
  UserSubscription,
} from '@/types/subscription'
import request from '@/utils/request'

/** 订阅计划 GET /subscriptions/plans */
export function getSubscriptionPlans(): Promise<SubscriptionPlan[]> {
  return request.get<SubscriptionPlan[]>('/subscriptions/plans')
}

/** 创建订阅 POST /subscriptions */
export function createSubscription(
  data: CreateSubscriptionPayload,
): Promise<CreateSubscriptionResult> {
  return request.post<CreateSubscriptionResult>('/subscriptions', data)
}

/** 我的订阅 GET /subscriptions/my */
export function getMySubscriptions(): Promise<UserSubscription[]> {
  return request.get<UserSubscription[]>('/subscriptions/my')
}

/** 暂停订阅 PUT /subscriptions/{id}/pause */
export function pauseSubscription(id: number): Promise<UserSubscription> {
  return request.put<UserSubscription>(`/subscriptions/${id}/pause`)
}

/** 跳过本次配送 PUT /subscriptions/{id}/skip */
export function skipSubscription(id: number): Promise<UserSubscription> {
  return request.put<UserSubscription>(`/subscriptions/${id}/skip`)
}

/** 取消订阅 PUT /subscriptions/{id}/cancel */
export function cancelSubscription(id: number): Promise<UserSubscription> {
  return request.put<UserSubscription>(`/subscriptions/${id}/cancel`)
}
