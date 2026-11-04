package com.coffee.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coffee.admin.dto.AdminProductDTO;
import com.coffee.admin.dto.AdminProductQueryDTO;
import com.coffee.admin.dto.AdminSkuDTO;
import com.coffee.admin.service.AdminProductService;
import com.coffee.admin.vo.AdminProductDetailVO;
import com.coffee.admin.vo.AdminProductVO;
import com.coffee.catalog.entity.CoffeeProduct;
import com.coffee.catalog.entity.CoffeeSku;
import com.coffee.catalog.mapper.CoffeeProductMapper;
import com.coffee.catalog.mapper.CoffeeSkuMapper;
import com.coffee.catalog.vo.SkuVO;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.inventory.entity.InventoryLog;
import com.coffee.inventory.mapper.InventoryLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 后台商品管理实现
 */
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final CoffeeProductMapper productMapper;
    private final CoffeeSkuMapper skuMapper;
    private final InventoryLogMapper inventoryLogMapper;

    @Override
    public IPage<AdminProductVO> getProductPage(AdminProductQueryDTO query) {
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : Math.min(query.getSize(), 100);
        String keyword = StringUtils.hasText(query.getKeyword()) ? query.getKeyword().trim() : null;
        String roastLevel = StringUtils.hasText(query.getRoastLevel()) ? query.getRoastLevel() : null;
        return productMapper.selectAdminProductPage(new Page<>(page, size), keyword, roastLevel, query.getStatus());
    }

    @Override
    public AdminProductDetailVO getProductDetail(Long id) {
        CoffeeProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }

        AdminProductDetailVO vo = new AdminProductDetailVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setOrigin(product.getOrigin());
        vo.setRoastLevel(product.getRoastLevel());
        vo.setFlavorTags(splitTags(product.getFlavorTags()));
        vo.setProcessMethod(product.getProcessMethod());
        vo.setDescription(product.getDescription());
        vo.setCoverUrl(product.getCoverUrl());
        vo.setStatus(product.getStatus());
        vo.setCreateTime(product.getCreateTime());

        List<CoffeeSku> skus = skuMapper.selectList(
                new LambdaQueryWrapper<CoffeeSku>()
                        .eq(CoffeeSku::getProductId, id)
                        .orderByAsc(CoffeeSku::getId)
        );
        vo.setSkus(skus.stream().map(this::toSkuVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(AdminProductDTO dto) {
        // 手动校验 SKU 列表不能为空
        if (dto.getSkus() == null || dto.getSkus().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "商品必须包含至少一个 SKU");
        }

        // 1. 插入主商品
        CoffeeProduct product = new CoffeeProduct();
        applyMainFields(product, dto);
        product.setStatus(1); // 默认上架
        productMapper.insert(product);

        // 2. 插入 SKU 列表
        for (AdminSkuDTO skuDTO : dto.getSkus()) {
            CoffeeSku sku = new CoffeeSku();
            sku.setProductId(product.getId());
            sku.setWeight(skuDTO.getWeight());
            sku.setGrindType(skuDTO.getGrindType());
            sku.setPrice(skuDTO.getPrice());
            sku.setStock(skuDTO.getStock());
            skuMapper.insert(sku);
            recordStockChange(sku.getId(), sku.getStock());
        }
        return product.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long id, AdminProductDTO dto) {
        CoffeeProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }

        applyMainFields(product, dto);
        productMapper.updateById(product);

        // 传入 skus 时同步 SKU：有 id 更新，无 id 新增；未传入的旧 SKU 保留
        if (dto.getSkus() != null) {
            syncSkus(id, dto.getSkus());
        }
    }

    @Override
    public void updateProductStatus(Long id, Integer status) {
        CoffeeProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "商品状态参数错误");
        }
        product.setStatus(status);
        productMapper.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        CoffeeProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        // 逻辑删除商品与其 SKU；订单项已保存快照，不受影响
        productMapper.deleteById(id);
        skuMapper.delete(new LambdaQueryWrapper<CoffeeSku>().eq(CoffeeSku::getProductId, id));
    }

    private void applyMainFields(CoffeeProduct product, AdminProductDTO dto) {
        product.setName(dto.getName());
        product.setOrigin(dto.getOrigin());
        product.setRoastLevel(dto.getRoastLevel());
        product.setFlavorTags(dto.getFlavorTags());
        product.setProcessMethod(dto.getProcessMethod());
        product.setDescription(dto.getDescription());
        product.setCoverUrl(dto.getCoverUrl());
    }

    private void syncSkus(Long productId, List<AdminSkuDTO> skus) {
        List<CoffeeSku> existingList = skuMapper.selectList(
                new LambdaQueryWrapper<CoffeeSku>().eq(CoffeeSku::getProductId, productId)
        );
        Map<Long, CoffeeSku> existingMap = existingList.stream()
                .collect(Collectors.toMap(CoffeeSku::getId, Function.identity()));

        for (AdminSkuDTO skuDTO : skus) {
            if (skuDTO.getId() == null) {
                CoffeeSku sku = new CoffeeSku();
                sku.setProductId(productId);
                sku.setWeight(skuDTO.getWeight());
                sku.setGrindType(skuDTO.getGrindType());
                sku.setPrice(skuDTO.getPrice());
                sku.setStock(skuDTO.getStock());
                skuMapper.insert(sku);
                recordStockChange(sku.getId(), sku.getStock());
                continue;
            }

            CoffeeSku existing = existingMap.remove(skuDTO.getId());
            if (existing == null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "SKU 不存在或不属于当前商品");
            }

            int oldStock = existing.getStock() == null ? 0 : existing.getStock();
            int newStock = skuDTO.getStock() == null ? 0 : skuDTO.getStock();
            existing.setWeight(skuDTO.getWeight());
            existing.setGrindType(skuDTO.getGrindType());
            existing.setPrice(skuDTO.getPrice());
            existing.setStock(newStock);

            // @Version 乐观锁；数据库被并发修改时更新失败
            int updated = skuMapper.updateById(existing);
            if (updated == 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "SKU 库存已被其他操作修改，请刷新后重试");
            }
            recordStockChange(existing.getId(), newStock - oldStock);
        }
    }

    private void recordStockChange(Long skuId, int changeNum) {
        if (changeNum == 0) {
            return;
        }
        InventoryLog log = new InventoryLog();
        log.setSkuId(skuId);
        log.setChangeNum(changeNum);
        log.setType("ADJUST");
        inventoryLogMapper.insert(log);
    }

    private List<String> splitTags(String flavorTags) {
        if (!StringUtils.hasText(flavorTags)) {
            return List.of();
        }
        return Arrays.stream(flavorTags.split("[,，]"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    private SkuVO toSkuVO(CoffeeSku sku) {
        SkuVO vo = new SkuVO();
        vo.setId(sku.getId());
        vo.setWeight(sku.getWeight());
        vo.setGrindType(sku.getGrindType());
        vo.setPrice(sku.getPrice());
        vo.setStock(sku.getStock());
        return vo;
    }
}
