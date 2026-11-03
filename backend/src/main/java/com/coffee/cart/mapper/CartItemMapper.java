package com.coffee.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.cart.entity.CartItem;
import com.coffee.cart.vo.CartItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    // 根据购物车 ID 查询关联的商品、SKU 信息
    List<CartItemVO> selectCartItemVOs(@Param("cartId") Long cartId);
}