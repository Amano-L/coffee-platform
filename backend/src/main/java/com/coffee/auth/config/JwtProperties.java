package com.coffee.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置，绑定 application.yml 的 jwt.* 前缀
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    // 签名密钥
    private String secret;

    // Token 过期秒数
    private Long expireSeconds;

    // 请求头名称
    private String header;

    // 请求头前缀
    private String prefix;
}