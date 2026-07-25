// 生产总览模块：读取后端聚合指标和最近设备状态。
import { http } from './http'
import type { Device } from './devices'
export interface DashboardSummary { activeOrders: number; onlineDevices: number; todayOutput: number; qualityRate: number; devices: Device[] }
// 后端统一返回 ApiResponse，前台需要取出外层 data 才是 DashboardSummary。
export const getDashboardSummary = () => http.get<{ data: DashboardSummary }>('/dashboard/summary').then((r) => r.data.data)
