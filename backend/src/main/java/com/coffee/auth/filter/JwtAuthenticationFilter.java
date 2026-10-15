package com.coffee.auth.filter;

import com.coffee.auth.util.JwtUtil;
import com.coffee.common.constant.RedisKeyConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 取请求头
        String header = request.getHeader(jwtUtil.getHeader());
        if (header == null || !header.startsWith(jwtUtil.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取 token
        String token = header.substring(jwtUtil.getPrefix().length());

        // 解析 token，失败放行
        Long userId;
        try {
            userId = jwtUtil.parseUserId(token);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        // 查 Redis，确认 Token 未被登出
        String key = RedisKeyConstant.authTokenKey(userId);
        String redisToken = stringRedisTemplate.opsForValue().get(key);
        if (redisToken == null || !redisToken.equals(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 设置认证信息，principal 为 userId
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
