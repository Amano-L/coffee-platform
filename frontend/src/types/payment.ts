export interface Payment {
  payNo: string
  orderNo: string
  amount: number
  status: string
  payTime?: string
}

/** 后端模拟回调入参，sign 固定为 SUCCESS_SIGN（仅学习/演示环境） */
export interface PaymentCallbackPayload {
  payNo: string
  orderNo: string
  amount: number
  status: string
  sign: string
}
