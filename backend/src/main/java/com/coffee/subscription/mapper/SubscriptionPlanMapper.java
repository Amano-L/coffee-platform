package com.coffee.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.subscription.entity.SubscriptionPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订阅计划 Mapper
 */
@Mapper
public interface SubscriptionPlanMapper extends BaseMapper<SubscriptionPlan> {
}