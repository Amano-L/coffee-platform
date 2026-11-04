package com.coffee.payment.service;

import com.coffee.payment.dto.PaymentCallbackDTO;
import com.coffee.payment.vo.PaymentVO;

/**
 * 支付业务接口
 */
public interface PaymentService {
    // 发起支付
    PaymentVO createPayment(Long userId, String orderNo);
    
    // 处理支付回调
    void handleCallback(PaymentCallbackDTO dto);

    // 查询支付单状态
    PaymentVO getPaymentStatus(Long userId, String payNo);
}