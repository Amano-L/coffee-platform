package com.coffee.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 创建订单入参
 */
@Data
public class CreateOrderDTO {

    @NotNull(message = "请选择收货地址")
    private Long addressId;

    @NotEmpty(message = "请选择要结算的商品")
    private List<Long> cartItemIds;
}