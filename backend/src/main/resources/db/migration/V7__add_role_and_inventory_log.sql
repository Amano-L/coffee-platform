-- 1. 给 user 表增加角色字段
ALTER TABLE `user` ADD COLUMN `role` VARCHAR(32) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN' AFTER `status`;

-- 2. 创建库存流水表
CREATE TABLE `inventory_log` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
                                 `order_no` VARCHAR(64) DEFAULT NULL COMMENT '关联订单号',
                                 `change_num` INT NOT NULL COMMENT '变更数量：正数增加，负数减少',
                                 `type` VARCHAR(32) NOT NULL COMMENT '类型：PRESERVE预占/CONFIRM扣减/RELEASE释放',
                                 `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_sku_id` (`sku_id`),
                                 KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';