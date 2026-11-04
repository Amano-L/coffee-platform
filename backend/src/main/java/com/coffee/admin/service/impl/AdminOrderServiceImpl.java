package com.coffee.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coffee.admin.service.AdminOrderService;
import com.coffee.admin.vo.AdminOrderVO;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.order.entity.CoffeeOrder;
import com.coffee.order.mapper.CoffeeOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 后台订单管理实现
 */
@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final CoffeeOrderMapper orderMapper;

    @Override
    public IPage<AdminOrderVO> getOrderPage(Integer page, Integer size) {
        Page<CoffeeOrder> orderPage = new Page<>(page, size);
        // 按创建时间倒序
        IPage<CoffeeOrder> result = orderMapper.selectPage(orderPage,
                new LambdaQueryWrapper<CoffeeOrder>().orderByDesc(CoffeeOrder::getCreateTime));

        return result.convert(order -> {
            AdminOrderVO vo = new AdminOrderVO();
            vo.setOrderNo(order.getOrderNo());
            vo.setUserId(order.getUserId());
            vo.setPayAmount(order.getPayAmount());
            vo.setStatus(order.getStatus());
            vo.setCreateTime(order.getCreateTime());
            return vo;
        });
    }

    @Override
    public void shipOrder(String orderNo) {
        CoffeeOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<CoffeeOrder>().eq(CoffeeOrder::getOrderNo, orderNo)
        );

        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }

        if (!"已支付".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有已支付的订单才能发货");
        }

        // 更新状态为已发货
        order.setStatus("已发货");
        orderMapper.updateById(order);
    }
}