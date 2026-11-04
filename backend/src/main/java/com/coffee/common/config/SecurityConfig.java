package com.coffee.common.config;

import com.coffee.auth.filter.JwtAuthenticationFilter;
import com.coffee.auth.security.RestAuthenticationEntryPoint;
import com.coffee.common.result.Result;
import com.coffee.common.result.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    // 注入 Spring 托管的 ObjectMapper
    private final ObjectMapper objectMapper;

    // 白名单路径，无需登录即可访问
    private static final String[] WHITE_LIST = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/products/**",
            "/api/v1/subscriptions/plans",
            "/api/v1/payments/callback",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/health"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // 后台接口需要 ADMIN 权限
                        .requestMatchers("/api/v1/inventory/**").hasRole("ADMIN") // 库存接口仅管理员
                        .anyRequest().authenticated()
                )
                // 无权限时的处理
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            Result<Void> result = Result.failed(ResultCode.FORBIDDEN);
                            // 使用注入的 objectMapper
                            response.getWriter().write(objectMapper.writeValueAsString(result));
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // BCrypt 密码编码器，注册与登录共用
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}