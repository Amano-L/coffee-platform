package com.coffee.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.admin.vo.AdminOrderVO;

/**
 * 后台订单管理接口
 */
public interface AdminOrderService {
    IPage<AdminOrderVO> getOrderPage(Integer page, Integer size);
    void shipOrder(String orderNo);
}