import { ref } from 'vue'
import { defineStore } from 'pinia'

import {
  cancelSubscription,
  createSubscription,
  getMySubscriptions,
  getSubscriptionPlans,
  pauseSubscription,
  skipSubscription,
} from '@/api/subscription'
import type {
  CreateSubscriptionPayload,
  CreateSubscriptionResult,
  SubscriptionPlan,
  UserSubscription,
} from '@/types/subscription'

export const useSubscriptionStore = defineStore('subscription', () => {
  const plans = ref<SubscriptionPlan[]>([])
  const mySubscriptions = ref<UserSubscription[]>([])
  const plansLoading = ref(false)
  const myLoading = ref(false)

  async function fetchPlans(force = false): Promise<SubscriptionPlan[]> {
    if (plans.value.length > 0 && !force) return plans.value
    plansLoading.value = true
    try {
      plans.value = await getSubscriptionPlans()
      return plans.value
    } finally {
      plansLoading.value = false
    }
  }

  async function fetchMySubscriptions(force = false): Promise<UserSubscription[]> {
    if (mySubscriptions.value.length > 0 && !force) return mySubscriptions.value
    myLoading.value = true
    try {
      mySubscriptions.value = await getMySubscriptions()
      return mySubscriptions.value
    } finally {
      myLoading.value = false
    }
  }

  async function create(
    payload: CreateSubscriptionPayload,
  ): Promise<CreateSubscriptionResult> {
    const result = await createSubscription(payload)
    await fetchMySubscriptions(true)
    return result
  }

  async function pause(id: number): Promise<void> {
    await pauseSubscription(id)
    await fetchMySubscriptions(true)
  }

  async function skip(id: number): Promise<void> {
    await skipSubscription(id)
    await fetchMySubscriptions(true)
  }

  async function cancel(id: number): Promise<void> {
    await cancelSubscription(id)
    await fetchMySubscriptions(true)
  }

  function reset(): void {
    plans.value = []
    mySubscriptions.value = []
  }

  return {
    plans,
    mySubscriptions,
    plansLoading,
    myLoading,
    fetchPlans,
    fetchMySubscriptions,
    create,
    pause,
    skip,
    cancel,
    reset,
  }
})
