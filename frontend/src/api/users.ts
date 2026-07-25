import { http } from './http'
import type { UserRole } from './auth'

export interface ManagedUser { id: number; username: string; displayName: string; role: UserRole; department: string | null }
export interface UserForm { username: string; password: string; displayName: string; role: UserRole; department: string }
export const listUsers = () => http.get<{ data: ManagedUser[] }>('/users').then((response) => response.data.data)
export const createUser = (form: UserForm) => http.post<{ data: ManagedUser }>('/users', form).then((response) => response.data.data)
export const updateUser = (id: number, form: UserForm) => http.put<{ data: ManagedUser }>(`/users/${id}`, form).then((response) => response.data.data)
export const deleteUser = (id: number) => http.delete(`/users/${id}`)
