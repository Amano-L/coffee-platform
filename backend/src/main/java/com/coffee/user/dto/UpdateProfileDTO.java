package com.coffee.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改个人信息入参
 */
@Data
public class UpdateProfileDTO {

    // 昵称，最长 64 位
    @Size(max = 64, message = "昵称长度不能超过 64 位")
    private String nickname;

    // 头像 URL，最长 255 位
    @Size(max = 255, message = "头像 URL 长度不能超过 255 位")
    private String avatarUrl;

    // 手机号，修改时必须符合格式
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
