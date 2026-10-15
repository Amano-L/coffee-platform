package com.coffee.auth.controller;

import com.coffee.auth.dto.LoginDTO;
import com.coffee.auth.dto.RegisterDTO;
import com.coffee.auth.service.AuthService;
import com.coffee.auth.vo.LoginVO;
import com.coffee.auth.vo.RegisterVO;
import com.coffee.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 用户注册
    @PostMapping("/register")
    public Result<RegisterVO> register(@Valid @RequestBody RegisterDTO dto) {
        RegisterVO vo = authService.register(dto);
        return Result.success(vo);
    }

    // 用户登录
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = authService.login(dto);
        return Result.success(vo);
    }

    // 用户登出
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
