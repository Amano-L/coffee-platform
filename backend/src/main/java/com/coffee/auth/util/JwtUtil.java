package com.coffee.auth.util;

import com.coffee.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类，封装生成与解析
 */
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private final SecretKey secretKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    //  生成 Token，subject 为 userId
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpireSeconds() * 1000);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // 从 Token 中解析 userId，失败抛 JwtException
    public Long parseUserId(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

    // 校验 Token 是否有效
    public boolean validateToken(String token){
        try {
            parseUserId(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 获取配置的过期秒数，供 Redis 设置 TTL 使用
    public Long getExpireSeconds() {
        return jwtProperties.getExpireSeconds();
    }

    // 获取请求头名称
    public String getHeader() {
        return jwtProperties.getHeader();
    }

    // 获取请求头前缀
    public String getPrefix() {
        return jwtProperties.getPrefix();
    }
}
