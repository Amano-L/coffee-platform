package com.coffee.cart.controller;

import com.coffee.cart.dto.AddCartItemDTO;
import com.coffee.cart.dto.UpdateCartItemDTO;
import com.coffee.cart.service.CartService;
import com.coffee.cart.vo.CartVO;
import com.coffee.common.result.Result;
import com.coffee.common.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车接口
 */
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<CartVO> getCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(cartService.getCart(userId));
    }

    @PostMapping("/items")
    public Result<Void> addItem(@Valid @RequestBody AddCartItemDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.addItem(userId, dto);
        return Result.success();
    }

    @PutMapping("/items/{id}")
    public Result<Void> updateItem(@PathVariable Long id, @Valid @RequestBody UpdateCartItemDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.updateItem(userId, id, dto);
        return Result.success();
    }

    @DeleteMapping("/items/{id}")
    public Result<Void> removeItem(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.removeItem(userId, id);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> clearCart() {
        Long userId = SecurityUtil.getCurrentUserId();
        cartService.clearCart(userId);
        return Result.success();
    }
}