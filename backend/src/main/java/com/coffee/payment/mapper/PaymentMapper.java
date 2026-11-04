package com.coffee.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coffee.payment.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}