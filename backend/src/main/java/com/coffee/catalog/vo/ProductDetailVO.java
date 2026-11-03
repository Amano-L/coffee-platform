package com.coffee.catalog.vo;

import lombok.Data;
import java.util.List;

/**
 * 商品详情出参
 */
@Data
public class ProductDetailVO {
    private Long id;
    private String name;
    private String origin;
    private String roastLevel;
    private List<String> flavorTags;
    private String processMethod;
    private String description;
    private String coverUrl;
    private List<SkuVO> skus;
}