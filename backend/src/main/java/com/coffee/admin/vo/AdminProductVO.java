package com.coffee.admin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 后台商品列表出参。
 */
@Data
public class AdminProductVO {
    private Long id;
    private String name;
    private String origin;
    private String roastLevel;
    private String flavorTags;
    private String processMethod;
    private BigDecimal minPrice;
    private String coverUrl;
    private Integer status;
    private LocalDateTime createTime;
}
