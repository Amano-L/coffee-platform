package com.coffee.auth.vo;

import lombok.Data;

/**
 * 注册出参
 */
@Data
public class RegisterVO {

    // 用户 ID
    private Long userId;

    // 用户名
    private String username;
}