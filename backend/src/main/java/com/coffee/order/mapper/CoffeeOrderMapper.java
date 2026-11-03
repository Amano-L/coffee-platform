package com.coffee.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.order.entity.CoffeeOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoffeeOrderMapper extends BaseMapper<CoffeeOrder> {
}