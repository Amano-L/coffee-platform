package com.coffee.catalog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.catalog.entity.CoffeeSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * SKU Mapper
 */
@Mapper
public interface CoffeeSkuMapper extends BaseMapper<CoffeeSku> {

    // 乐观锁扣减库存
    @Update("UPDATE coffee_sku SET stock = stock - #{num}, version = version + 1 " +
            "WHERE id = #{id} AND stock >= #{num} AND version = #{version} AND deleted = 0")
    int deductStock(@Param("id") Long id, @Param("num") Integer num, @Param("version") Integer version);

    // 增加库存（取消订单时释放）
    @Update("UPDATE coffee_sku SET stock = stock + #{num} WHERE id = #{id} AND deleted = 0")
    int addStock(@Param("id") Long id, @Param("num") Integer num);
}