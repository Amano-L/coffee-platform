package com.coffee.catalog.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品列表出参
 */
@Data
public class ProductVO {
    private Long id;
    private String name;
    private String origin;
    private String roastLevel;
    // 前端需要看数组，但为了简化当前后台对接，暂时返回逗号字符串
    private String flavorTags;
    // 数据库是逗号分隔，VO 是列表
    // private List<String> flavorTags;
    private String processMethod;
    private BigDecimal minPrice;
    private String coverUrl;

}