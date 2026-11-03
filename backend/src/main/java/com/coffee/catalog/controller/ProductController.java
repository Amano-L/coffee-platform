package com.coffee.catalog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.catalog.dto.ProductQueryDTO;
import com.coffee.catalog.service.ProductService;
import com.coffee.catalog.vo.ProductDetailVO;
import com.coffee.catalog.vo.ProductVO;
import com.coffee.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品接口
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 商品列表（白名单放行）
    @GetMapping
    public Result<IPage<ProductVO>> getList(ProductQueryDTO query) {
        return Result.success(productService.getProductPage(query));
    }

    // 商品详情（白名单放行）
    @GetMapping("/{id}")
    public Result<ProductDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }
}