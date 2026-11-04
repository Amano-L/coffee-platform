import { storeToRefs } from 'pinia'

import { useCartStore } from '@/stores/cart'

/** 购物车状态与动作的统一入口，避免组件重复 storeToRefs */
export function useCart() {
  const cartStore = useCartStore()
  const {
    items,
    totalAmount,
    loading,
    totalQuantity,
    checkedItems,
    checkedCount,
    checkedTotalAmount,
    allChecked,
  } = storeToRefs(cartStore)

  return {
    cartStore,
    items,
    totalAmount,
    loading,
    totalQuantity,
    checkedItems,
    checkedCount,
    checkedTotalAmount,
    allChecked,
  }
}
