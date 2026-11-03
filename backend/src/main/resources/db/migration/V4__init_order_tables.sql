-- 订单表
CREATE TABLE `coffee_order` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
                                `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
                                `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
                                `status` VARCHAR(32) NOT NULL COMMENT '状态：待支付/已支付/待发货/已发货/已完成/已取消/退款中/已退款',
                                `receiver_name` VARCHAR(64) NOT NULL COMMENT '收货人姓名',
                                `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人手机号',
                                `receiver_address` VARCHAR(255) NOT NULL COMMENT '收货地址',
                                `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_order_no` (`order_no`),
                                KEY `idx_user_id` (`user_id`),
                                KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
CREATE TABLE `coffee_order_item` (
                                     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `order_id` BIGINT NOT NULL COMMENT '订单ID',
                                     `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
                                     `product_name` VARCHAR(128) NOT NULL COMMENT '商品名称快照',
                                     `price` DECIMAL(10,2) NOT NULL COMMENT '单价快照',
                                     `quantity` INT NOT NULL COMMENT '数量',
                                     `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';