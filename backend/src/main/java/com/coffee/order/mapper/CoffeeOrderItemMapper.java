package com.coffee.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.order.entity.CoffeeOrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoffeeOrderItemMapper extends BaseMapper<CoffeeOrderItem> {
}