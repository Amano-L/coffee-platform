package com.coffee.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coffee.admin.vo.AdminSubscriptionVO;
import com.coffee.subscription.entity.UserSubscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户订阅 Mapper
 */
@Mapper
public interface UserSubscriptionMapper extends BaseMapper<UserSubscription> {

    // 后台订阅分页，关联用户与订阅计划
    IPage<AdminSubscriptionVO> selectAdminSubscriptionPage(
            Page<AdminSubscriptionVO> page,
            @Param("status") String status
    );
}