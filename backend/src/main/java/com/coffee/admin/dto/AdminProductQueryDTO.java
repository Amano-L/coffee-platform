package com.coffee.admin.dto;

import lombok.Data;

/**
 * 后台商品列表查询入参。
 */
@Data
public class AdminProductQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private String roastLevel;
    /** 上架状态：1 上架，0 下架，不传则查询全部 */
    private Integer status;
}
