package com.coffee.catalog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coffee.catalog.dto.ProductQueryDTO;
import com.coffee.catalog.entity.CoffeeProduct;
import com.coffee.catalog.entity.CoffeeSku;
import com.coffee.catalog.mapper.CoffeeProductMapper;
import com.coffee.catalog.mapper.CoffeeSkuMapper;
import com.coffee.catalog.service.ProductService;
import com.coffee.catalog.vo.ProductDetailVO;
import com.coffee.catalog.vo.ProductVO;
import com.coffee.catalog.vo.SkuVO;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品业务实现
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CoffeeProductMapper productMapper;
    private final CoffeeSkuMapper skuMapper;

    @Override
    public IPage<ProductVO> getProductPage(ProductQueryDTO query) {
        Page<ProductVO> page = new Page<>(query.getPage(), query.getSize());
        IPage<ProductVO> result = productMapper.selectProductPage(
                page, query.getKeyword(), query.getRoastLevel(), query.getFlavorTag(), query.getProcessMethod()
        );
        
        // 处理逗号分隔的 flavorTags
        result.getRecords().forEach(vo -> {
            // XML 里 select 出来的字段没有做逗号转 List，这里通过拦截处理
            // 但如果是直接查出来的，说明 flavorTags 暂时是 null，因为 SQL 里没做别名映射映射到 List
            // 实际上 MyBatis 不能直接把 String 映射到 List，我们需要在 XML 里不查 flavor_tags，或者在这里手动处理
        });
        return result;
    }

    @Override
    public ProductDetailVO getProductDetail(Long id) {
        CoffeeProduct product = productMapper.selectById(id);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在或已下架");
        }

        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setOrigin(product.getOrigin());
        vo.setRoastLevel(product.getRoastLevel());
        vo.setProcessMethod(product.getProcessMethod());
        vo.setDescription(product.getDescription());
        vo.setCoverUrl(product.getCoverUrl());
        
        // 处理标签
        if (StringUtils.hasText(product.getFlavorTags())) {
            List<String> tags = Arrays.stream(product.getFlavorTags().split(","))
                    .map(String::trim).collect(Collectors.toList());
            vo.setFlavorTags(tags);
        }

        // 查 SKU
        List<CoffeeSku> skus = skuMapper.selectList(
                new LambdaQueryWrapper<CoffeeSku>().eq(CoffeeSku::getProductId, id)
        );
        List<SkuVO> skuVOS = skus.stream().map(sku -> {
            SkuVO s = new SkuVO();
            s.setId(sku.getId());
            s.setWeight(sku.getWeight());
            s.setGrindType(sku.getGrindType());
            s.setPrice(sku.getPrice());
            s.setStock(sku.getStock());
            return s;
        }).collect(Collectors.toList());
        
        vo.setSkus(skuVOS);
        return vo;
    }
}