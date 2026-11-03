package com.coffee.cart.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车整体出参
 */
@Data
public class CartVO {
    private List<CartItemVO> items;
    // 已勾选商品的总金额
    private BigDecimal totalAmount;
}