package com.coffee.inventory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.common.result.Result;
import com.coffee.inventory.dto.AdjustStockDTO;
import com.coffee.inventory.entity.InventoryLog;
import com.coffee.inventory.service.InventoryService;
import com.coffee.inventory.vo.InventorySkuVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 库存管理接口（管理端）
 */
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/skus/{skuId}")
    public Result<InventorySkuVO> getSkuStock(@PathVariable Long skuId) {
        return Result.success(inventoryService.getSkuStock(skuId));
    }

    @PutMapping("/skus/{skuId}")
    public Result<InventorySkuVO> adjustStock(@PathVariable Long skuId, @Valid @RequestBody AdjustStockDTO dto) {
        return Result.success(inventoryService.adjustStock(skuId, dto));
    }

    @GetMapping("/logs")
    public Result<IPage<InventoryLog>> getLogs(
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(inventoryService.getLogs(skuId, orderNo, page, size));
    }
}