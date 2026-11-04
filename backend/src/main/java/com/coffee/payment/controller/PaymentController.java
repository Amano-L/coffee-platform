package com.coffee.payment.controller;

import com.coffee.common.result.Result;
import com.coffee.common.util.SecurityUtil;
import com.coffee.payment.dto.PaymentCallbackDTO;
import com.coffee.payment.service.PaymentService;
import com.coffee.payment.vo.PaymentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付接口
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 发起支付
    @PostMapping("/{orderNo}/pay")
    public Result<PaymentVO> createPayment(@PathVariable String orderNo) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(paymentService.createPayment(userId, orderNo));
    }

    // 支付回调（白名单，无需 Token，因为第三方平台打过来的）
    @PostMapping("/callback")
    public Result<Void> callback(@Valid @RequestBody PaymentCallbackDTO dto) {
        paymentService.handleCallback(dto);
        return Result.success();
    }

    // 查询支付状态
    @GetMapping("/{payNo}")
    public Result<PaymentVO> getPaymentStatus(@PathVariable String payNo) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(paymentService.getPaymentStatus(userId, payNo));
    }
}