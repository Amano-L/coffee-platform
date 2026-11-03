package com.coffee.catalog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.catalog.dto.ProductQueryDTO;
import com.coffee.catalog.vo.ProductDetailVO;
import com.coffee.catalog.vo.ProductVO;

/**
 * 商品业务接口
 */
public interface ProductService {
    IPage<ProductVO> getProductPage(ProductQueryDTO query);
    ProductDetailVO getProductDetail(Long id);
}