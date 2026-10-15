package com.coffee.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coffee.auth.dto.LoginDTO;
import com.coffee.auth.dto.RegisterDTO;
import com.coffee.auth.service.AuthService;
import com.coffee.auth.util.JwtUtil;
import com.coffee.auth.vo.LoginVO;
import com.coffee.auth.vo.RegisterVO;
import com.coffee.auth.vo.UserInfoVO;
import com.coffee.common.constant.RedisKeyConstant;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.common.util.SecurityUtil;
import com.coffee.user.entity.User;
import com.coffee.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 认证业务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RegisterVO register(RegisterDTO dto) {
        // 用户名唯一校验
        Long usernameCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (usernameCount > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 手机号唯一校验
        Long phoneCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone())
        );
        if (phoneCount > 0) {
            throw new BusinessException(ResultCode.PHONE_EXISTS);
        }

        // 构造实体
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(1);

        // 插入
        userMapper.insert(user);

        // 返回
        RegisterVO vo = new RegisterVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        return vo;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 查用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException(ResultCode.INVALID_CREDENTIALS);
        }

        // 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.INVALID_CREDENTIALS);
        }

        // 校验状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId());

        // 存 Redis
        String key = RedisKeyConstant.authTokenKey(user.getId());
        stringRedisTemplate.opsForValue().set(
                key,
                token,
                Duration.ofSeconds(jwtUtil.getExpireSeconds())
        );

        // 组装响应
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatarUrl(user.getAvatarUrl());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserInfo(userInfo);
        return vo;
    }

    @Override
    public void logout() {
        // 取当前登录用户 ID
        Long userId = SecurityUtil.getCurrentUserId();

        // 删除 Redis 中的 Token，用户立即下线
        String key = RedisKeyConstant.authTokenKey(userId);
        stringRedisTemplate.delete(key);
    }
}