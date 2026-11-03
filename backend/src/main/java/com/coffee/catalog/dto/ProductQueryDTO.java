package com.coffee.catalog.dto;

import lombok.Data;

/**
 * 商品查询入参
 */
@Data
public class ProductQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private String roastLevel;
    private String flavorTag;
    private String processMethod;
}