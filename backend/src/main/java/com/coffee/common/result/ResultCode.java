package com.coffee.common.result;

import lombok.Getter;
/*
    错误码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(0, "success"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或 Token 失效"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // 认证相关
    USERNAME_EXISTS(4007, "用户名已存在"),
    PHONE_EXISTS(4008, "手机号已存在"),
    INVALID_CREDENTIALS(4009, "用户名或密码错误"),
    ACCOUNT_DISABLED(4010, "账号已被禁用"),

    SYSTEM_ERROR(5000, "系统异常");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
