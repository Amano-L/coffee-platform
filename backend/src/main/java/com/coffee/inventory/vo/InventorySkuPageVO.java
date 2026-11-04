package com.coffee.inventory.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 后台库存 SKU 列表出参。
 */
@Data
public class InventorySkuPageVO {
    private Long skuId;
    private Long productId;
    private String productName;
    private String origin;
    private String weight;
    private String grindType;
    private BigDecimal price;
    private Integer stock;
    private Integer version;
}
