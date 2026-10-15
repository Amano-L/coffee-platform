package com.coffee.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper，继承 MyBatis-Plus 通用 CRUD
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
