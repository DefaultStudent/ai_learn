// 所有前台 REST 请求从这里创建，避免在组件内重复拼接 API 地址。
import axios from 'axios'

// Vite 开发环境由 proxy 转发 /api 到本机 Spring Boot 8080 端口。
export const http = axios.create({ baseURL: '/api', timeout: 8000, withCredentials: true })
export interface ApiResponse<T> { data: T; message?: string; timestamp?: string }
