package com.coffee.admin.service;

import com.coffee.admin.dto.AdminProductDTO;

/**
 * 后台商品管理接口
 */
public interface AdminProductService {
    void createProduct(AdminProductDTO dto);

    void updateProduct(Long id, AdminProductDTO dto);
    void updateProductStatus(Long id, Integer status);
}