import { http } from './http'
export interface SystemLog { id: number; level: string; module: string; action: string; message: string; operatorName: string | null; createdAt: string }
export interface LogPage { items: SystemLog[]; page: number; size: number; total: number; totalPages: number }
export const listLogs = () => http.get<{ data: LogPage }>('/logs').then((response) => response.data.data)
