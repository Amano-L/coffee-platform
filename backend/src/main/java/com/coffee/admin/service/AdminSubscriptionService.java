package com.coffee.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coffee.admin.dto.AdminSubscriptionQueryDTO;
import com.coffee.admin.vo.AdminSubscriptionVO;

/**
 * 后台订阅管理接口
 */
public interface AdminSubscriptionService {

    IPage<AdminSubscriptionVO> getSubscriptionPage(AdminSubscriptionQueryDTO query);

    void pauseSubscription(Long id);

    void cancelSubscription(Long id);
}
