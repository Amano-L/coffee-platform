package com.coffee.user.controller;

import com.coffee.common.result.Result;
import com.coffee.common.util.SecurityUtil;
import com.coffee.user.dto.AddressDTO;
import com.coffee.user.dto.UpdatePreferenceDTO;
import com.coffee.user.dto.UpdateProfileDTO;
import com.coffee.user.service.UserService;
import com.coffee.user.vo.AddressVO;
import com.coffee.user.vo.PreferenceVO;
import com.coffee.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    // 获取当前登录用户信息
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        UserVO vo = userService.getById(userId);
        return Result.success(vo);
    }

    // 修改当前登录用户信息
    @PutMapping("/me")
    public Result<UserVO> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        UserVO vo = userService.updateProfile(userId, dto);
        return Result.success(vo);
    }

    // 获取当前用户咖啡偏好
    @GetMapping("/preference")
    public Result<PreferenceVO> getPreference() {
        Long userId = SecurityUtil.getCurrentUserId();
        PreferenceVO vo = userService.getPreference(userId);
        return Result.success(vo);
    }

    // 保存当前用户咖啡偏好
    @PutMapping("/preference")
    public Result<PreferenceVO> updatePreference(@Valid @RequestBody UpdatePreferenceDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        PreferenceVO vo = userService.updatePreference(userId, dto);
        return Result.success(vo);
    }

    // 地址列表
    @GetMapping("/addresses")
    public Result<List<AddressVO>> getAddressList() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(userService.getAddressList(userId));
    }

    // 新增地址
    @PostMapping("/addresses")
    public Result<AddressVO> addAddress(@Valid @RequestBody AddressDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(userService.addAddress(userId, dto));
    }

    // 修改地址
    @PutMapping("/addresses/{id}")
    public Result<AddressVO> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(userService.updateAddress(userId, id, dto));
    }

    // 删除地址
    @DeleteMapping("/addresses/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.deleteAddress(userId, id);
        return Result.success();
    }

    // 设置默认
    @PutMapping("/addresses/{id}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.setDefaultAddress(userId, id);
        return Result.success();
    }
}
