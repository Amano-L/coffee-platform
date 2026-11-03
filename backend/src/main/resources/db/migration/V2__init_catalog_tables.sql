-- 咖啡商品表
CREATE TABLE `coffee_product` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `name` VARCHAR(128) NOT NULL COMMENT '商品名称',
                                  `origin` VARCHAR(64) DEFAULT NULL COMMENT '产区',
                                  `roast_level` VARCHAR(32) DEFAULT NULL COMMENT '烘焙度：LIGHT/MEDIUM/DARK',
                                  `flavor_tags` VARCHAR(255) DEFAULT NULL COMMENT '风味标签',
                                  `process_method` VARCHAR(32) DEFAULT NULL COMMENT '处理法',
                                  `description` TEXT COMMENT '描述',
                                  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
                                  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
                                  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_roast_level` (`roast_level`),
                                  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咖啡商品表';

-- 咖啡 SKU 表
CREATE TABLE `coffee_sku` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `product_id` BIGINT NOT NULL COMMENT '商品ID',
                              `weight` VARCHAR(32) NOT NULL COMMENT '重量：250g/500g',
                              `grind_type` VARCHAR(32) NOT NULL COMMENT '整豆/研磨',
                              `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
                              `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
                              `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
                              PRIMARY KEY (`id`),
                              KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咖啡SKU表';