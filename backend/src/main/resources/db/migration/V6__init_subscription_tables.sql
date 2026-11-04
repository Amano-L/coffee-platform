-- 订阅计划表
CREATE TABLE `subscription_plan` (
                                     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `name` VARCHAR(64) NOT NULL COMMENT '计划名称',
                                     `sku_id` BIGINT NOT NULL COMMENT '绑定的SKU ID',
                                     `period` VARCHAR(32) NOT NULL COMMENT '周期：WEEKLY/BIWEEKLY/MONTHLY',
                                     `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
                                     `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
                                     `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订阅计划表';

-- 用户订阅表
CREATE TABLE `user_subscription` (
                                     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                     `plan_id` BIGINT NOT NULL COMMENT '订阅计划ID',
                                     `status` VARCHAR(32) NOT NULL COMMENT '状态：ACTIVE/PAUSED/CANCELLED',
                                     `next_delivery_date` DATE NOT NULL COMMENT '下次配送日期',
                                     `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_user_id` (`user_id`),
                                     KEY `idx_status` (`status`),
                                     KEY `idx_next_delivery_date` (`next_delivery_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户订阅表';

-- 订阅订单关联表 (记录生成过的订阅订单)
CREATE TABLE `subscription_order` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `user_subscription_id` BIGINT NOT NULL COMMENT '用户订阅ID',
                                      `order_id` BIGINT NOT NULL COMMENT '订单ID',
                                      `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
                                      `delivery_date` DATE NOT NULL COMMENT '配送日期',
                                      `status` VARCHAR(32) NOT NULL COMMENT '状态：待支付/已支付/已取消',
                                      `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_user_subscription_id` (`user_subscription_id`),
                                      KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订阅订单关联表';