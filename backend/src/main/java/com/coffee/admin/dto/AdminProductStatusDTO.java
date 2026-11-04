package com.coffee.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 后台-修改商品状态入参
 */
@Data
public class AdminProductStatusDTO {
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态错误")
    @Max(value = 1, message = "状态错误")
    private Integer status;
}