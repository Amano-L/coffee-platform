package com.coffee.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

/**
 * 后台-商品聚合入参
 */
@Data
public class AdminProductDTO {
    @NotBlank(message = "商品名称不能为空")
    private String name;
    
    private String origin;
    private String roastLevel;
    private String flavorTags;
    private String processMethod;
    private String description;
    private String coverUrl;

    // 必须包含至少一个 SKU
    @Valid
    // @NotEmpty(message = "商品必须包含至少一个 SKU")
    private List<AdminSkuDTO> skus;
}