package com.coffee.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.admin.dto.AdminProductDTO;
import com.coffee.admin.dto.AdminProductQueryDTO;
import com.coffee.admin.dto.AdminProductStatusDTO;
import com.coffee.admin.service.AdminProductService;
import com.coffee.admin.vo.AdminProductDetailVO;
import com.coffee.admin.vo.AdminProductVO;
import com.coffee.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台商品管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    // 商品列表（包含下架商品）
    @GetMapping
    public Result<IPage<AdminProductVO>> getProductPage(AdminProductQueryDTO query) {
        return Result.success(adminProductService.getProductPage(query));
    }

    // 商品详情（包含下架商品与 SKU）
    @GetMapping("/{id}")
    public Result<AdminProductDetailVO> getProductDetail(@PathVariable Long id) {
        return Result.success(adminProductService.getProductDetail(id));
    }

    // 新增商品（含SKU列表）
    @PostMapping
    public Result<Map<String, Object>> createProduct(@Valid @RequestBody AdminProductDTO dto) {
        Long id = adminProductService.createProduct(dto);
        return Result.success(Map.of("id", id, "name", dto.getName()));
    }

    // 修改商品信息（可同时更新/新增 SKU）
    @PutMapping("/{id}")
    public Result<Map<String, Object>> updateProduct(@PathVariable Long id, @Valid @RequestBody AdminProductDTO dto) {
        adminProductService.updateProduct(id, dto);
        return Result.success(Map.of("id", id, "name", dto.getName()));
    }

    // 上下架商品
    @PutMapping("/{id}/status")
    public Result<Map<String, Object>> updateStatus(@PathVariable Long id, @Valid @RequestBody AdminProductStatusDTO dto) {
        adminProductService.updateProductStatus(id, dto.getStatus());
        return Result.success(Map.of("id", id, "status", dto.getStatus()));
    }

    // 逻辑删除商品及其 SKU
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return Result.success();
    }
}
