package com.coffee.inventory.vo;

import lombok.Data;

/**
 * 库存 SKU 出参
 */
@Data
public class InventorySkuVO {
    private Long skuId;
    private String productName;
    private String weight;
    private Integer stock;
    private Integer version;
}