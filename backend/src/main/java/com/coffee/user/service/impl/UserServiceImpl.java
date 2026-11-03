package com.coffee.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.user.dto.UpdatePreferenceDTO;
import com.coffee.user.dto.UpdateProfileDTO;
import com.coffee.user.entity.User;
import com.coffee.user.entity.UserPreference;
import com.coffee.user.mapper.UserMapper;
import com.coffee.user.mapper.UserPreferenceMapper;
import com.coffee.user.service.UserService;

import com.coffee.user.vo.PreferenceVO;
import com.coffee.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserPreferenceMapper userPreferenceMapper;

    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return toVO(user);
    }

    @Override
    public UserVO updateProfile(Long userId, UpdateProfileDTO dto) {
        // 如果修改了手机号，检查是否被他人占用
        if (dto.getPhone() != null) {
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getPhone, dto.getPhone())
                            .ne(User::getId, userId)
            );
            if (count > 0) {
                throw new BusinessException(ResultCode.PHONE_EXISTS);
            }
        }

        // 构造实体，只设置 ID 和需要修改的字段
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setNickname(dto.getNickname());
        updateUser.setAvatarUrl(dto.getAvatarUrl());
        updateUser.setPhone(dto.getPhone());

        // 执行更新（MyBatis-Plus 默认只更新非 null 字段）
        userMapper.updateById(updateUser);

        // 查最新数据并返回
        User latestUser = userMapper.selectById(userId);
        return toVO(latestUser);
    }

    @Override
    public PreferenceVO getPreference(Long userId) {
        UserPreference preference = userPreferenceMapper.selectOne(
                new LambdaQueryWrapper<UserPreference>().eq(UserPreference::getUserId, userId)
        );

        PreferenceVO vo = new PreferenceVO();
        if (preference != null) {
            vo.setRoastLevel(preference.getRoastLevel());
            vo.setProcessMethod(preference.getProcessMethod());
            // 转换逗号分隔的字符串为 List
            if (StringUtils.hasText(preference.getFlavorTags())) {
                List<String> tags = Arrays.stream(preference.getFlavorTags().split(","))
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList());
                vo.setFlavorTags(tags);
            }
        }
        return vo;
    }

    @Override
    public PreferenceVO updatePreference(Long userId, UpdatePreferenceDTO dto) {
        // 查是否已有记录
        UserPreference preference = userPreferenceMapper.selectOne(
                new LambdaQueryWrapper<UserPreference>().eq(UserPreference::getUserId, userId)
        );

        // 将 List 转为逗号分隔的字符串
        String flavorTagsStr = null;
        if (dto.getFlavorTags() != null) {
            flavorTagsStr = dto.getFlavorTags().stream()
                    .filter(StringUtils::hasText)
                    .collect(Collectors.joining(","));
        }

        boolean isNew = false;
        if (preference == null) {
            preference = new UserPreference();
            preference.setUserId(userId);
            isNew = true;
        }

        preference.setRoastLevel(dto.getRoastLevel());
        preference.setProcessMethod(dto.getProcessMethod());
        preference.setFlavorTags(flavorTagsStr);

        if (isNew) {
            userPreferenceMapper.insert(preference);
        } else {
            userPreferenceMapper.updateById(preference);
        }

        // 复用查询逻辑返回最新数据
        return getPreference(userId);
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
