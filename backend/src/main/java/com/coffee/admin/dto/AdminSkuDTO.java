package com.coffee.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 后台-SKU入参
 */
@Data
public class AdminSkuDTO {
    /** 已有 SKU 的 ID；为空表示新增 SKU */
    private Long id;

    @NotBlank(message = "重量规格不能为空")
    private String weight;

    @NotBlank(message = "研磨方式不能为空")
    private String grindType;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能小于0")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;
}