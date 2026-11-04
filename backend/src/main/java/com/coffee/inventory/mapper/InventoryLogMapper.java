package com.coffee.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.inventory.entity.InventoryLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存流水 Mapper
 */
@Mapper
public interface InventoryLogMapper extends BaseMapper<InventoryLog> {
}