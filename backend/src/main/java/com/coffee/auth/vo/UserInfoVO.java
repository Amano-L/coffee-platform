package com.coffee.auth.vo;

import lombok.Data;

/**
 * 登录响应中的用户信息
 */
@Data
public class UserInfoVO {

    // 用户 ID
    private Long id;

    // 用户名
    private String username;

    // 昵称
    private String nickname;

    // 头像
    private String avatarUrl;
}