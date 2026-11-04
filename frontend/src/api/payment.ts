import type { Payment, PaymentCallbackPayload } from '@/types/payment'
import request from '@/utils/request'

/** 发起支付 POST /payments/{orderNo}/pay */
export function createPayment(orderNo: string): Promise<Payment> {
  return request.post<Payment>(`/payments/${orderNo}/pay`)
}

/** 查询支付状态 GET /payments/{payNo} */
export function getPaymentStatus(payNo: string): Promise<Payment> {
  return request.get<Payment>(`/payments/${payNo}`)
}

/**
 * 模拟支付回调 POST /payments/callback
 * 仅用于学习/演示环境（后端 sign 约定为 SUCCESS_SIGN）。
 * 真实支付应由第三方服务端异步回调，前端不应调用。
 */
export function mockPaymentCallback(data: PaymentCallbackPayload): Promise<null> {
  return request.post<null>('/payments/callback', data, { silent: true })
}
