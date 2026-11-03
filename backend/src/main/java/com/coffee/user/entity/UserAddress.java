package com.coffee.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收货地址实体，对应表 user_address
 */
@Data
@TableName("user_address")
public class UserAddress {

    // 主键
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户 ID
    private Long userId;

    // 收货人姓名
    private String receiverName;

    // 收货人手机号
    private String phone;

    // 省
    private String province;

    // 市
    private String city;

    // 区
    private String district;

    // 详细地址
    private String detailAddress;

    // 是否默认：0否 1是
    private Integer isDefault;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 逻辑删除
    @TableLogic
    private Integer deleted;
}