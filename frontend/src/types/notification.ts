export interface NotificationItem {
  id: number
  title: string
  content: string
  /** ORDER / SUBSCRIPTION / SYSTEM */
  type?: string | null
  isRead: number
  createTime: string
}
