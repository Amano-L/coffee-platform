package com.coffee.user.vo;

import lombok.Data;

/**
 * 收货地址出参
 */
@Data
public class AddressVO {

    private Long id;
    private String receiverName;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
}