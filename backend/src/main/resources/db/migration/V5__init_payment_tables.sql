-- 支付表
CREATE TABLE `payment` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                           `order_id` BIGINT NOT NULL COMMENT '订单ID',
                           `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
                           `pay_no` VARCHAR(64) NOT NULL COMMENT '支付单号',
                           `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
                           `status` VARCHAR(32) NOT NULL COMMENT '状态：待支付/已支付/已退款',
                           `callback_content` TEXT COMMENT '回调内容',
                           `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_pay_no` (`pay_no`),
                           KEY `idx_order_id` (`order_id`),
                           KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';