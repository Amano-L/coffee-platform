import type {
  AddCartItemPayload,
  AddCartItemResult,
  Cart,
  UpdateCartItemPayload,
  UpdateCartItemResult,
} from '@/types/cart'
import request from '@/utils/request'

/** 获取购物车 GET /cart */
export function getCart(): Promise<Cart> {
  return request.get<Cart>('/cart')
}

/** 加入购物车 POST /cart/items */
export function addCartItem(data: AddCartItemPayload): Promise<AddCartItemResult> {
  return request.post<AddCartItemResult>('/cart/items', data)
}

/** 修改购物车项 PUT /cart/items/{id} */
export function updateCartItem(
  id: number,
  data: UpdateCartItemPayload,
): Promise<UpdateCartItemResult> {
  return request.put<UpdateCartItemResult>(`/cart/items/${id}`, data)
}

/** 删除购物车项 DELETE /cart/items/{id} */
export function removeCartItem(id: number): Promise<null> {
  return request.delete<null>(`/cart/items/${id}`)
}

/** 清空购物车 DELETE /cart */
export function clearCart(): Promise<null> {
  return request.delete<null>('/cart')
}
