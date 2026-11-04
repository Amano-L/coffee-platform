package com.coffee.cart.service;

import com.coffee.cart.dto.AddCartItemDTO;
import com.coffee.cart.dto.UpdateCartItemDTO;
import com.coffee.cart.vo.AddCartItemVO;
import com.coffee.cart.vo.CartVO;
import com.coffee.cart.vo.UpdateCartItemVO;

/**
 * 购物车业务接口
 */
public interface CartService {
    AddCartItemVO addItem(Long userId, AddCartItemDTO dto);
    CartVO getCart(Long userId);
    UpdateCartItemVO updateItem(Long userId, Long cartItemId, UpdateCartItemDTO dto);
    void removeItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
}