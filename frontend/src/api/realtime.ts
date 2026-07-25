import { http } from './http'

export const getApprovalRealtimeToken = () => http.get<{ data: string }>('/realtime/token').then((response) => response.data.data)
