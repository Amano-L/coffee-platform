package com.coffee.admin.service.impl;

import com.coffee.admin.dto.AdminProductDTO;
import com.coffee.admin.dto.AdminSkuDTO;
import com.coffee.admin.service.AdminProductService;
import com.coffee.catalog.entity.CoffeeProduct;
import com.coffee.catalog.entity.CoffeeSku;
import com.coffee.catalog.mapper.CoffeeProductMapper;
import com.coffee.catalog.mapper.CoffeeSkuMapper;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 后台商品管理实现
 */
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final CoffeeProductMapper productMapper;
    private final CoffeeSkuMapper skuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProduct(AdminProductDTO dto) {
        // 手动校验 SKU 列表不能为空
        if (dto.getSkus() == null || dto.getSkus().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "商品必须包含至少一个 SKU");
        }
        // 1. 插入主商品
        CoffeeProduct product = new CoffeeProduct();
        product.setName(dto.getName());
        product.setOrigin(dto.getOrigin());
        product.setRoastLevel(dto.getRoastLevel());
        product.setFlavorTags(dto.getFlavorTags());
        product.setProcessMethod(dto.getProcessMethod());
        product.setDescription(dto.getDescription());
        product.setCoverUrl(dto.getCoverUrl());
        product.setStatus(1); // 默认上架
        
        productMapper.insert(product);

        // 2. 插入 SKU 列表
        for (AdminSkuDTO skuDTO : dto.getSkus()) {
            CoffeeSku sku = new CoffeeSku();
            sku.setProductId(product.getId()); // 绑定刚才生成的主键
            sku.setWeight(skuDTO.getWeight());
            sku.setGrindType(skuDTO.getGrindType());
            sku.setPrice(skuDTO.getPrice());
            sku.setStock(skuDTO.getStock());
            skuMapper.insert(sku);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long id, AdminProductDTO dto) {
        CoffeeProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }

        // 只更新主表字段，不处理 SKU 列表
        product.setName(dto.getName());
        product.setOrigin(dto.getOrigin());
        product.setRoastLevel(dto.getRoastLevel());
        product.setFlavorTags(dto.getFlavorTags());
        product.setProcessMethod(dto.getProcessMethod());
        product.setDescription(dto.getDescription());
        product.setCoverUrl(dto.getCoverUrl());

        productMapper.updateById(product);
    }

    @Override
    public void updateProductStatus(Long id, Integer status) {
        CoffeeProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        product.setStatus(status);
        productMapper.updateById(product);
    }

}