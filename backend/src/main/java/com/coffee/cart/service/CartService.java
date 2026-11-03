package com.coffee.cart.service;

import com.coffee.cart.dto.AddCartItemDTO;
import com.coffee.cart.dto.UpdateCartItemDTO;
import com.coffee.cart.vo.CartVO;

/**
 * 购物车业务接口
 */
public interface CartService {
    void addItem(Long userId, AddCartItemDTO dto);
    CartVO getCart(Long userId);
    void updateItem(Long userId, Long cartItemId, UpdateCartItemDTO dto);
    void removeItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
}