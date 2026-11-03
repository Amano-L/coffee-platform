package com.coffee.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.cart.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}