package com.coffee.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coffee.common.exception.BusinessException;
import com.coffee.common.result.ResultCode;
import com.coffee.order.entity.CoffeeOrder;
import com.coffee.order.mapper.CoffeeOrderMapper;
import com.coffee.order.service.OrderService;
import com.coffee.payment.dto.PaymentCallbackDTO;
import com.coffee.payment.entity.Payment;
import com.coffee.payment.mapper.PaymentMapper;
import com.coffee.payment.service.PaymentService;
import com.coffee.payment.vo.PaymentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 支付业务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final CoffeeOrderMapper orderMapper;
    private final OrderService orderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO createPayment(Long userId, String orderNo) {
        CoffeeOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<CoffeeOrder>()
                        .eq(CoffeeOrder::getOrderNo, orderNo)
                        .eq(CoffeeOrder::getUserId, userId)
        );

        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        if (!"待支付".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "当前订单状态不允许支付");
        }

        // 查是否已有待支付的支付单
        Payment existingPayment = paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getOrderNo, orderNo)
                        .eq(Payment::getStatus, "待支付")
        );

        Payment payment = existingPayment;
        if (payment == null) {
            // 生成新的支付单
            payment = new Payment();
            payment.setOrderId(order.getId());
            payment.setOrderNo(orderNo);
            payment.setPayNo("PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + ThreadLocalRandom.current().nextInt(1000, 9999));
            payment.setAmount(order.getPayAmount());
            payment.setStatus("待支付");
            paymentMapper.insert(payment);
        }

        PaymentVO vo = new PaymentVO();
        vo.setPayNo(payment.getPayNo());
        vo.setOrderNo(payment.getOrderNo());
        vo.setAmount(payment.getAmount());
        vo.setStatus(payment.getStatus());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleCallback(PaymentCallbackDTO dto) {
        log.info("收到支付回调：{}", dto);

        // 1. 验签（模拟通过，真实环境必须校验）
        if (!"SUCCESS_SIGN".equals(dto.getSign())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "签名验证失败");
        }

        // 2. 查支付单
        Payment payment = paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>().eq(Payment::getPayNo, dto.getPayNo())
        );

        if (payment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "支付单不存在");
        }

        // 3. 幂等处理：如果已经支付成功，直接返回
        if ("已支付".equals(payment.getStatus())) {
            log.info("支付单 {} 已处理过，直接返回成功", dto.getPayNo());
            return;
        }

        // 4. 金额校验
        if (payment.getAmount().compareTo(dto.getAmount()) != 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "支付金额不匹配");
        }

        // 5. 更新支付单状态
        payment.setStatus("已支付");
        payment.setCallbackContent(dto.toString());
        paymentMapper.updateById(payment);

        // 6. 更新订单状态
        orderService.updateOrderToPaid(payment.getOrderNo());
        
        log.info("支付单 {} 处理成功，订单 {} 已支付", dto.getPayNo(), payment.getOrderNo());
    }

    @Override
    public PaymentVO getPaymentStatus(Long userId, String payNo) {
        Payment payment = paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>().eq(Payment::getPayNo, payNo)
        );

        if (payment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "支付单不存在");
        }

        // 防越权：确保该支付单对应的订单属于当前用户
        CoffeeOrder order = orderMapper.selectById(payment.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权查看此支付单");
        }

        PaymentVO vo = new PaymentVO();
        vo.setPayNo(payment.getPayNo());
        vo.setOrderNo(payment.getOrderNo());
        vo.setAmount(payment.getAmount());
        vo.setStatus(payment.getStatus());
        return vo;
    }
}