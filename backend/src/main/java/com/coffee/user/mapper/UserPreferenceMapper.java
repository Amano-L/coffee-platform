package com.coffee.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.user.entity.UserPreference;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户偏好 Mapper
 */
@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {
}