package com.coffee.admin.controller;

import com.coffee.admin.dto.AdminProductDTO;
import com.coffee.admin.dto.AdminProductStatusDTO;
import com.coffee.admin.service.AdminProductService;
import com.coffee.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台商品管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    // 新增商品（含SKU列表）
    @PostMapping
    public Result<Void> createProduct(@Valid @RequestBody AdminProductDTO dto) {
        adminProductService.createProduct(dto);
        return Result.success();
    }

    // 修改商品信息
    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @Valid @RequestBody AdminProductDTO dto) {
        adminProductService.updateProduct(id, dto);
        return Result.success();
    }

    // 上下架商品
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody AdminProductStatusDTO dto) {
        adminProductService.updateProductStatus(id, dto.getStatus());
        return Result.success();
    }
}