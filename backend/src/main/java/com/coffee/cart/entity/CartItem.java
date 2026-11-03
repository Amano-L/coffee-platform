package com.coffee.cart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 购物车项实体
 */
@Data
@TableName("cart_item")
public class CartItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long cartId;
    private Long skuId;
    private Integer quantity;
    private Integer checked;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}