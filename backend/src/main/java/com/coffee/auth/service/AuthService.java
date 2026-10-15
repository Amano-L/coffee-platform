package com.coffee.auth.service;

import com.coffee.auth.dto.LoginDTO;
import com.coffee.auth.dto.RegisterDTO;
import com.coffee.auth.vo.LoginVO;
import com.coffee.auth.vo.RegisterVO;
import jakarta.validation.Valid;

/**
 * 认证业务接口
 */
public interface AuthService {

    // 注册新用户
    RegisterVO register(@Valid RegisterDTO dto);

    // 用户登录
    LoginVO login(@Valid LoginDTO dto);

    // 用户登出
    void logout();
}
