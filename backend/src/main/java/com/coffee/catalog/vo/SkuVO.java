package com.coffee.catalog.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * SKU 出参
 */
@Data
public class SkuVO {
    private Long id;
    private String weight;
    private String grindType;
    private BigDecimal price;
    private Integer stock;
}