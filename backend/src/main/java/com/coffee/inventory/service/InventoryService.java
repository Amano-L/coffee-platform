package com.coffee.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.inventory.dto.AdjustStockDTO;
import com.coffee.inventory.entity.InventoryLog;
import com.coffee.inventory.vo.InventorySkuPageVO;
import com.coffee.inventory.vo.InventorySkuVO;

/**
 * 库存业务接口
 */
public interface InventoryService {
    IPage<InventorySkuPageVO> getSkuPage(String keyword, Integer page, Integer size);

    InventorySkuVO getSkuStock(Long skuId);
    InventorySkuVO adjustStock(Long skuId, AdjustStockDTO dto);
    IPage<InventoryLog> getLogs(Long skuId, String orderNo, Integer page, Integer size);
}