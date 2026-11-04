package com.coffee.subscription.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.subscription.entity.UserSubscription;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户订阅 Mapper
 */
@Mapper
public interface UserSubscriptionMapper extends BaseMapper<UserSubscription> {
}