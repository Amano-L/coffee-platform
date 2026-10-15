package com.coffee.auth.vo;

import lombok.Data;

/**
 * 登录出参
 */
@Data
public class LoginVO {

    // JWT Token
    private String token;

    // 用户信息
    private UserInfoVO userInfo;
}