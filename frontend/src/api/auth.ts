import { http } from './http'

export type UserRole = 'SYSTEM_ADMIN' | 'ADMIN' | 'PRODUCTION_MANAGER' | 'DEVICE_MANAGER' | 'PRODUCTION_USER' | 'DEVICE_USER'
export interface CurrentUser { id: number; username: string; displayName: string; role: UserRole; department: string | null; permissions: string[] }
export interface LoginForm { username: string; password: string }
export const login = (form: LoginForm) => http.post<{ data: CurrentUser }>('/auth/login', form).then((response) => response.data.data)
export const currentUser = () => http.get<{ data: CurrentUser }>('/auth/me').then((response) => response.data.data)
export const logout = () => http.post('/auth/logout')
