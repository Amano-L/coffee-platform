package com.coffee.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户咖啡偏好实体，对应表 user_preference
 */
@Data
@TableName("user_preference")
public class UserPreference {

    // 主键
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户 ID
    private Long userId;

    // 烘焙度：LIGHT/MEDIUM/DARK
    private String roastLevel;

    // 风味标签，逗号分隔
    private String flavorTags;

    // 处理法：WASHED/NATURAL/HONEY
    private String processMethod;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 逻辑删除
    @TableLogic
    private Integer deleted;
}