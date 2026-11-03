package com.coffee.user.service;

import com.coffee.user.dto.UpdatePreferenceDTO;
import com.coffee.user.dto.UpdateProfileDTO;
import com.coffee.user.vo.PreferenceVO;
import com.coffee.user.vo.UserVO;
import jakarta.validation.Valid;

/**
 * 用户业务接口
 */
public interface UserService {

    // 根据 ID 查询用户信息
    UserVO getById(Long id);

    // 修改当前用户个人信息
    UserVO updateProfile(Long userId, UpdateProfileDTO dto);

    // 获取用户咖啡偏好
    PreferenceVO getPreference(Long userId);

    // 保存用户咖啡偏好
    PreferenceVO updatePreference(Long userId, UpdatePreferenceDTO dto);
}
