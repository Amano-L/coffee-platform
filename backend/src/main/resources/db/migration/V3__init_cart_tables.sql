-- 购物车主表
CREATE TABLE `cart` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `user_id` BIGINT NOT NULL COMMENT '用户ID',
                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 购物车项表
CREATE TABLE `cart_item` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `cart_id` BIGINT NOT NULL COMMENT '购物车ID',
                             `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
                             `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
                             `checked` TINYINT NOT NULL DEFAULT 1 COMMENT '是否勾选：0否 1是',
                             `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`),
                             KEY `idx_cart_id` (`cart_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车项表';