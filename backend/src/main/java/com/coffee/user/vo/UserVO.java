package com.coffee.user.vo;

import lombok.Data;

/**
 * 用户信息出参，不含密码等敏感字段
 */
@Data
public class UserVO {

    // 用户 ID
    private Long id;

    // 用户名
    private String username;

    // 昵称
    private String nickname;

    // 手机号
    private String phone;

    // 邮箱
    private String email;

    // 头像
    private String avatarUrl;

    // 状态：1正常 0禁用
    private Integer status;
}