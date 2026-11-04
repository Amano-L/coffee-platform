export interface CartItem {
  id: number
  skuId: number
  productName: string
  weight: string
  grindType: string
  price: number
  quantity: number
  checked: number
  coverUrl?: string | null
}

export interface Cart {
  items: CartItem[]
  totalAmount: number
}

export interface AddCartItemPayload {
  skuId: number
  quantity: number
}

export interface AddCartItemResult {
  cartItemId: number
  skuId: number
  quantity: number
}

export interface UpdateCartItemPayload {
  quantity?: number
  checked?: number
}

export interface UpdateCartItemResult {
  id: number
  quantity: number
  checked: number
}
