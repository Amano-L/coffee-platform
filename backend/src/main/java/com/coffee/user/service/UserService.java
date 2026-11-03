package com.coffee.user.service;

import com.coffee.user.dto.AddressDTO;
import com.coffee.user.dto.UpdatePreferenceDTO;
import com.coffee.user.dto.UpdateProfileDTO;
import com.coffee.user.vo.AddressVO;
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

    // 查询用户地址列表
    java.util.List<AddressVO> getAddressList(Long userId);

    // 新增地址
    AddressVO addAddress(Long userId, AddressDTO dto);

    // 修改地址
    AddressVO updateAddress(Long userId, Long addressId, AddressDTO dto);

    // 删除地址
    void deleteAddress(Long userId, Long addressId);

    // 设置默认地址
    void setDefaultAddress(Long userId, Long addressId);
}
