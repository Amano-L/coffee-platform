package com.coffee.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.admin.dto.AdminProductDTO;
import com.coffee.admin.dto.AdminProductQueryDTO;
import com.coffee.admin.vo.AdminProductDetailVO;
import com.coffee.admin.vo.AdminProductVO;

/**
 * 后台商品管理接口
 */
public interface AdminProductService {

    IPage<AdminProductVO> getProductPage(AdminProductQueryDTO query);

    AdminProductDetailVO getProductDetail(Long id);

    Long createProduct(AdminProductDTO dto);

    void updateProduct(Long id, AdminProductDTO dto);

    void updateProductStatus(Long id, Integer status);

    void deleteProduct(Long id);
}
