package com.coffee.common.constant;

/**
 * Redis Key 常量
 */
public class RedisKeyConstant {

    // 认证 Token 前缀，完整 Key 为 auth:token:{userId}
    public static final String AUTH_TOKEN_PREFIX = "auth:token:";

    // 拼接用户 Token Key
    public static String authTokenKey(Long userId) {
        return AUTH_TOKEN_PREFIX + userId;
    }
}