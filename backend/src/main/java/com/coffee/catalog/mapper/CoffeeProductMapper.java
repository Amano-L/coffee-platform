package com.coffee.catalog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coffee.catalog.entity.CoffeeProduct;
import com.coffee.catalog.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品 Mapper
 */
@Mapper
public interface CoffeeProductMapper extends BaseMapper<CoffeeProduct> {

    // 分页查询商品列表，包含最低价格
    IPage<ProductVO> selectProductPage(
            Page<ProductVO> page,
            @Param("keyword") String keyword,
            @Param("roastLevel") String roastLevel,
            @Param("flavorTag") String flavorTag,
            @Param("processMethod") String processMethod
    );
}