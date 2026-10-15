package com.coffee.user.service.impl;

import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.user.entity.User;
import com.coffee.user.mapper.UserMapper;
import com.coffee.user.service.UserService;

import com.coffee.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return toVO(user);
    }

    // User 实体转 UserVO，过滤敏感字段
    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setStatus(user.getStatus());
        return vo;
    }
}
