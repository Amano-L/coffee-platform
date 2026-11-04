package com.coffee.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coffee.catalog.entity.CoffeeProduct;
import com.coffee.catalog.entity.CoffeeSku;
import com.coffee.catalog.mapper.CoffeeProductMapper;
import com.coffee.catalog.mapper.CoffeeSkuMapper;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.inventory.dto.AdjustStockDTO;
import com.coffee.inventory.entity.InventoryLog;
import com.coffee.inventory.mapper.InventoryLogMapper;
import com.coffee.inventory.service.InventoryService;
import com.coffee.inventory.vo.InventorySkuPageVO;
import com.coffee.inventory.vo.InventorySkuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 库存业务实现
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final CoffeeSkuMapper skuMapper;
    private final CoffeeProductMapper productMapper;
    private final InventoryLogMapper inventoryLogMapper;

    @Override
    public IPage<InventorySkuPageVO> getSkuPage(String keyword, Integer page, Integer size) {
        int current = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : Math.min(size, 100);
        String search = StringUtils.hasText(keyword) ? keyword.trim() : null;
        return skuMapper.selectInventoryPage(new Page<>(current, pageSize), search);
    }

    @Override
    public InventorySkuVO getSkuStock(Long skuId) {
        CoffeeSku sku = skuMapper.selectById(skuId);
        if (sku == null) throw new BusinessException(ResultCode.NOT_FOUND, "SKU不存在");
        
        CoffeeProduct product = productMapper.selectById(sku.getProductId());
        
        InventorySkuVO vo = new InventorySkuVO();
        vo.setSkuId(sku.getId());
        vo.setProductName(product != null ? product.getName() : "未知");
        vo.setWeight(sku.getWeight());
        vo.setStock(sku.getStock());
        vo.setVersion(sku.getVersion());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InventorySkuVO adjustStock(Long skuId, AdjustStockDTO dto) {
        CoffeeSku sku = skuMapper.selectById(skuId);
        if (sku == null) throw new BusinessException(ResultCode.NOT_FOUND, "SKU不存在");

        int newStock = sku.getStock() + dto.getChangeNum();
        if (newStock < 0) throw new BusinessException(ResultCode.BAD_REQUEST, "调整后库存不能小于0");

        // 使用自定义 SQL 直接更新，避开 updateById 的乐观锁校验
        skuMapper.updateStockDirectly(skuId, newStock);

        // 记录流水
        InventoryLog log = new InventoryLog();
        log.setSkuId(skuId);
        log.setChangeNum(dto.getChangeNum());
        log.setType("ADJUST"); // 后台手动调整
        inventoryLogMapper.insert(log);

        return getSkuStock(skuId);
    }

    @Override
    public IPage<InventoryLog> getLogs(Long skuId, String orderNo, Integer page, Integer size) {
        Page<InventoryLog> p = new Page<>(page, size);
        LambdaQueryWrapper<InventoryLog> wrapper = new LambdaQueryWrapper<InventoryLog>()
                .orderByDesc(InventoryLog::getCreateTime);
        if (skuId != null) wrapper.eq(InventoryLog::getSkuId, skuId);
        if (StringUtils.hasText(orderNo)) wrapper.eq(InventoryLog::getOrderNo, orderNo);

        return inventoryLogMapper.selectPage(p, wrapper);
    }
}