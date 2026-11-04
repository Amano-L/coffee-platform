package com.coffee.cart.vo;

import lombok.Data;

/**
 * 加入购物车出参。
 */
@Data
public class AddCartItemVO {
    private Long cartItemId;
    private Long skuId;
    private Integer quantity;
}
