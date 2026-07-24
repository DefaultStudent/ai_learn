// 生产总览模块：读取后端聚合指标并提供给 Dashboard 页面。
import { http } from './http'
export interface DashboardSummary { activeOrders: number; onlineDevices: number; todayOutput: number; qualityRate: number }
export const getDashboardSummary = () => http.get<DashboardSummary>('/dashboard/summary').then((r) => r.data)
