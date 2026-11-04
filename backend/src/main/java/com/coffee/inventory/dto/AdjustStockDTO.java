package com.coffee.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 调整库存入参
 */
@Data
public class AdjustStockDTO {
    @NotNull(message = "调整数量不能为空")
    private Integer changeNum;

    @NotBlank(message = "调整原因不能为空")
    private String reason;
}