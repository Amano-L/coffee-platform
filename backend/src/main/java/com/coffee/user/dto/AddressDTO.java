package com.coffee.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 收货地址新增/修改入参
 */
@Data
public class AddressDTO {

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 64, message = "收货人姓名过长")
    private String receiverName;

    @NotBlank(message = "收货人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址过长")
    private String detailAddress;

    // 是否默认：0否 1是（默认0）
    @Min(value = 0, message = "默认状态参数错误")
    @Max(value = 1, message = "默认状态参数错误")
    private Integer isDefault = 0;
}