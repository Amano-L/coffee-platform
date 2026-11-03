package com.coffee.catalog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.catalog.entity.CoffeeSku;
import org.apache.ibatis.annotations.Mapper;

/**
 * SKU Mapper
 */
@Mapper
public interface CoffeeSkuMapper extends BaseMapper<CoffeeSku> {
}