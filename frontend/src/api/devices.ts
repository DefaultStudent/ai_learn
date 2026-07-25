import { http } from './http'

// 设备模块的前台类型与 REST 请求封装。
export interface Device { id: number; code: string; name: string; status: string }
export interface DevicePage { items: Device[]; page: number; size: number; total: number; totalPages: number }
export interface DeviceForm { code: string; name: string }

export type DeviceSortField = 'id' | 'code' | 'name' | 'status'
export type SortDirection = 'asc' | 'desc'
export const listDevices = (params: { keyword?: string; status?: string; page?: number; size?: number; sortBy?: DeviceSortField; direction?: SortDirection }) =>
  http.get<{ data: DevicePage }>('/devices', { params }).then((response) => response.data.data)
export const createDevice = (form: DeviceForm) => http.post<{ data: Device }>('/devices', form).then((response) => response.data.data)
export const updateDevice = (id: number, form: DeviceForm) => http.put<{ data: Device }>(`/devices/${id}`, form).then((response) => response.data.data)
export const updateDeviceStatus = (id: number, status: string) => http.put<{ data: Device }>(`/devices/${id}/status`, { status }).then((response) => response.data.data)
export const deleteDevice = (id: number) => http.delete(`/devices/${id}`)
