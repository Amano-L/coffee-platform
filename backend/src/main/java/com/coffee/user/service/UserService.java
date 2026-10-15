package com.coffee.user.service;

import com.coffee.user.vo.UserVO;

/**
 * 用户业务接口
 */
public interface UserService {

    // 根据 ID 查询用户信息
    UserVO getById(Long id);
}
