package com.coffee.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应表 user
 */
@Data
@TableName("user")
public class User {

    // 主键
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户名
    private String username;

    // 密码
    private String password;

    // 手机号
    private String phone;

    // 邮箱
    private String email;

    // 昵称
    private String nickname;

    // 头像
    private String avatarUrl;

    // 状态：1正常 0禁用
    private Integer status;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 逻辑删除：0未删除 1已删除
    @TableLogic
    private Integer deleted;
}