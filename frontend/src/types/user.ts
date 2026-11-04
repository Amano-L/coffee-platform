/** 登录响应中的用户信息（后端 UserInfoVO，role 为后端实际返回字段） */
export interface UserInfo {
  id: number
  username: string
  nickname?: string | null
  avatarUrl?: string | null
  role?: string | null
}

/** 当前用户完整信息（GET /users/me） */
export interface UserProfile {
  id: number
  username: string
  nickname?: string | null
  phone?: string | null
  email?: string | null
  avatarUrl?: string | null
  status?: number
}

export interface LoginPayload {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  userInfo: UserInfo
}

export interface RegisterPayload {
  username: string
  password: string
  phone: string
  email?: string
}

export interface RegisterResult {
  userId: number
  username: string
}

export interface UpdateProfilePayload {
  nickname?: string
  avatarUrl?: string
  phone?: string
}

export interface Address {
  id: number
  receiverName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: number
}

export interface AddressPayload {
  receiverName: string
  phone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: number
}

export interface Preference {
  roastLevel?: string | null
  flavorTags?: string[] | null
  processMethod?: string | null
}

export interface PreferencePayload {
  roastLevel?: string
  flavorTags?: string[]
  processMethod?: string
}
