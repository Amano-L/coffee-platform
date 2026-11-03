package com.coffee.cart.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 购物车项出参
 */
@Data
public class CartItemVO {
    private Long id;
    private Long skuId;
    private String productName;
    private String weight;
    private String grindType;
    private BigDecimal price;
    private Integer quantity;
    private Integer checked;
    private String coverUrl;
}