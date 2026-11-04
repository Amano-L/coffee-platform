import type {
  Address,
  AddressPayload,
  Preference,
  PreferencePayload,
  UpdateProfilePayload,
  UserProfile,
} from '@/types/user'
import request from '@/utils/request'

/** 获取当前登录用户 GET /users/me */
export function getCurrentUser(): Promise<UserProfile> {
  return request.get<UserProfile>('/users/me')
}

/** 修改个人信息 PUT /users/me */
export function updateProfile(data: UpdateProfilePayload): Promise<UserProfile> {
  return request.put<UserProfile>('/users/me', data)
}

/** 收货地址列表 GET /users/addresses */
export function getAddresses(): Promise<Address[]> {
  return request.get<Address[]>('/users/addresses')
}

/** 新增收货地址 POST /users/addresses */
export function addAddress(data: AddressPayload): Promise<Address> {
  return request.post<Address>('/users/addresses', data)
}

/** 修改收货地址 PUT /users/addresses/{id} */
export function updateAddress(id: number, data: AddressPayload): Promise<Address> {
  return request.put<Address>(`/users/addresses/${id}`, data)
}

/** 删除收货地址 DELETE /users/addresses/{id} */
export function deleteAddress(id: number): Promise<null> {
  return request.delete<null>(`/users/addresses/${id}`)
}

/** 设置默认地址 PUT /users/addresses/{id}/default */
export function setDefaultAddress(id: number): Promise<{ id: number; isDefault: number }> {
  return request.put<{ id: number; isDefault: number }>(`/users/addresses/${id}/default`)
}

/** 获取咖啡偏好 GET /users/preference */
export function getPreference(): Promise<Preference> {
  return request.get<Preference>('/users/preference')
}

/** 保存咖啡偏好 PUT /users/preference */
export function updatePreference(data: PreferencePayload): Promise<Preference> {
  return request.put<Preference>('/users/preference', data)
}
