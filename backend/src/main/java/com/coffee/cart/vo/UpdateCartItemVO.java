package com.coffee.cart.vo;

import lombok.Data;

/**
 * 修改购物车项出参。
 */
@Data
public class UpdateCartItemVO {
    private Long id;
    private Integer quantity;
    private Integer checked;
}
