package com.coffee.user.controller;

import com.coffee.common.result.Result;
import com.coffee.user.service.UserService;
import com.coffee.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    // 根据 ID 查询用户
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO vo = userService.getById(id);
        return Result.success(vo);
    }


}
