package com.coffee.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.user.dto.AddressDTO;
import com.coffee.user.dto.UpdatePreferenceDTO;
import com.coffee.user.dto.UpdateProfileDTO;
import com.coffee.user.entity.User;
import com.coffee.user.entity.UserAddress;
import com.coffee.user.entity.UserPreference;
import com.coffee.user.mapper.UserAddressMapper;
import com.coffee.user.mapper.UserMapper;
import com.coffee.user.mapper.UserPreferenceMapper;
import com.coffee.user.service.UserService;

import com.coffee.user.vo.AddressVO;
import com.coffee.user.vo.PreferenceVO;
import com.coffee.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserPreferenceMapper userPreferenceMapper;

    private final UserAddressMapper userAddressMapper;

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

    @Override
    public List<AddressVO> getAddressList(Long userId) {
        List<UserAddress> list = userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .orderByDesc(UserAddress::getIsDefault)
                        .orderByDesc(UserAddress::getCreateTime)
        );
        return list.stream().map(this::toAddressVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddressVO addAddress(Long userId, AddressDTO dto) {
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        copyDtoToAddress(dto, address);

        // 如果设置为了默认地址，先把其他的取消默认
        if (address.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }

        userAddressMapper.insert(address);
        return toAddressVO(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddressVO updateAddress(Long userId, Long addressId, AddressDTO dto) {
        UserAddress address = getValidAddress(userId, addressId);

        copyDtoToAddress(dto, address);

        // 如果修改为了默认地址，取消其他的默认
        if (address.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }

        userAddressMapper.updateById(address);
        return toAddressVO(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = getValidAddress(userId, addressId);
        userAddressMapper.deleteById(address.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        UserAddress address = getValidAddress(userId, addressId);

        // 1. 清空所有默认
        clearDefaultAddress(userId);

        // 2. 设置当前为默认
        UserAddress update = new UserAddress();
        update.setId(address.getId());
        update.setIsDefault(1);
        userAddressMapper.updateById(update);
    }

    // ===== 私有辅助方法 =====

    // 获取校验过的地址（防止越权）
    private UserAddress getValidAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectOne(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getId, addressId)
                        .eq(UserAddress::getUserId, userId)
        );
        if (address == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "地址不存在");
        }
        return address;
    }

    // 清除用户的所有默认地址状态
    private void clearDefaultAddress(Long userId) {
        UserAddress reset = new UserAddress();
        reset.setIsDefault(0);
        userAddressMapper.update(reset, new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1)
        );
    }

    // DTO 转 Entity
    private void copyDtoToAddress(AddressDTO dto, UserAddress address) {
        address.setReceiverName(dto.getReceiverName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(dto.getIsDefault());
    }

    // Entity 转 VO
    private AddressVO toAddressVO(UserAddress address) {
        AddressVO vo = new AddressVO();
        vo.setId(address.getId());
        vo.setReceiverName(address.getReceiverName());
        vo.setPhone(address.getPhone());
        vo.setProvince(address.getProvince());
        vo.setCity(address.getCity());
        vo.setDistrict(address.getDistrict());
        vo.setDetailAddress(address.getDetailAddress());
        vo.setIsDefault(address.getIsDefault());
        return vo;
    }
}
