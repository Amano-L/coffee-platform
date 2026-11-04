package com.coffee.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.subscription.entity.SubscriptionOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订阅订单关联 Mapper
 */
@Mapper
public interface SubscriptionOrderMapper extends BaseMapper<SubscriptionOrder> {
}