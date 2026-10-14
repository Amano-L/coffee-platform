-- ============================================================
-- V1: 初始化用户模块表
-- 包含：user、user_address、user_preference
-- ============================================================

-- 用户表
CREATE TABLE `user` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `username` VARCHAR(64) NOT NULL COMMENT '用户名',
                        `password` VARCHAR(128) NOT NULL COMMENT 'BCrypt 密码',
                        `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
                        `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
                        `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
                        `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像',
                        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`),
                        UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 收货地址表
CREATE TABLE `user_address` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                `receiver_name` VARCHAR(64) NOT NULL COMMENT '收货人姓名',
                                `phone` VARCHAR(20) NOT NULL COMMENT '收货人手机号',
                                `province` VARCHAR(64) NOT NULL COMMENT '省',
                                `city` VARCHAR(64) NOT NULL COMMENT '市',
                                `district` VARCHAR(64) NOT NULL COMMENT '区',
                                `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
                                `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认：0否 1是',
                                `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
                                PRIMARY KEY (`id`),
                                KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 用户咖啡偏好表
CREATE TABLE `user_preference` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                   `roast_level` VARCHAR(32) DEFAULT NULL COMMENT '烘焙度：LIGHT/MEDIUM/DARK',
                                   `flavor_tags` VARCHAR(255) DEFAULT NULL COMMENT '风味标签，逗号分隔',
                                   `process_method` VARCHAR(32) DEFAULT NULL COMMENT '处理法：WASHED/NATURAL/HONEY',
                                   `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户咖啡偏好表';