package com.coffee.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 修改购物车项入参
 */
@Data
public class UpdateCartItemDTO {
    @Min(value = 1, message = "数量至少为 1")
    @Max(value = 99, message = "单品限购 99 件")
    private Integer quantity;

    @Min(value = 0, message = "勾选状态错误")
    @Max(value = 1, message = "勾选状态错误")
    private Integer checked;
}