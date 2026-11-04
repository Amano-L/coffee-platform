import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import {
  addCartItem,
  clearCart as clearCartApi,
  getCart,
  removeCartItem,
  updateCartItem,
} from '@/api/cart'
import type { Cart, CartItem, UpdateCartItemPayload } from '@/types/cart'
import { getToken } from '@/utils/auth'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const totalAmount = ref(0)
  const loading = ref(false)

  const totalQuantity = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const checkedItems = computed(() => items.value.filter((item) => item.checked === 1))
  const checkedCount = computed(() =>
    checkedItems.value.reduce((sum, item) => sum + item.quantity, 0),
  )
  const checkedTotalAmount = computed(() =>
    checkedItems.value.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0),
  )
  const allChecked = computed(
    () => items.value.length > 0 && items.value.every((item) => item.checked === 1),
  )

  function applyCart(cart: Cart): void {
    items.value = cart.items ?? []
    totalAmount.value = Number(cart.totalAmount ?? 0)
  }

  async function fetchCart(): Promise<void> {
    if (!getToken()) {
      items.value = []
      totalAmount.value = 0
      return
    }
    loading.value = true
    try {
      applyCart(await getCart())
    } finally {
      loading.value = false
    }
  }

  async function addItem(skuId: number, quantity: number): Promise<void> {
    await addCartItem({ skuId, quantity })
    await fetchCart()
  }

  async function updateItem(id: number, payload: UpdateCartItemPayload): Promise<void> {
    const target = items.value.find((item) => item.id === id)
    if (target) {
      if (payload.quantity !== undefined) target.quantity = payload.quantity
      if (payload.checked !== undefined) target.checked = payload.checked
    }
    try {
      await updateCartItem(id, payload)
    } catch (error) {
      await fetchCart().catch(() => undefined)
      throw error
    }
    await fetchCart()
  }

  async function toggleAll(checked: boolean): Promise<void> {
    const changed = items.value.filter((item) => item.checked !== (checked ? 1 : 0))
    if (changed.length === 0) return
    await Promise.all(changed.map((item) => updateCartItem(item.id, { checked: checked ? 1 : 0 })))
    await fetchCart()
  }

  async function removeItem(id: number): Promise<void> {
    await removeCartItem(id)
    await fetchCart()
  }

  async function clearCart(): Promise<void> {
    await clearCartApi()
    items.value = []
    totalAmount.value = 0
  }

  function reset(): void {
    items.value = []
    totalAmount.value = 0
  }

  return {
    items,
    totalAmount,
    loading,
    totalQuantity,
    checkedItems,
    checkedCount,
    checkedTotalAmount,
    allChecked,
    fetchCart,
    addItem,
    updateItem,
    toggleAll,
    removeItem,
    clearCart,
    reset,
  }
})
