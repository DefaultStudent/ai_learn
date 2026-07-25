import { defineStore } from 'pinia'
import { currentUser, login, logout, type CurrentUser, type LoginForm } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({ user: null as CurrentUser | null, ready: false }),
  getters: {
    isSystemAdmin: (state) => state.user?.role === 'SYSTEM_ADMIN',
    canManageDevices: (state) => ['SYSTEM_ADMIN', 'ADMIN'].includes(state.user?.role ?? ''),
    canProposeDeviceChanges: (state) => state.user?.role === 'DEVICE_MANAGER',
    canViewDevices: (state) => ['SYSTEM_ADMIN', 'ADMIN', 'DEVICE_MANAGER', 'DEVICE_USER'].includes(state.user?.role ?? ''),
    canViewProduction: (state) => ['SYSTEM_ADMIN', 'ADMIN', 'PRODUCTION_MANAGER', 'PRODUCTION_USER'].includes(state.user?.role ?? ''),
  },
  actions: {
    async restore() { try { this.user = await currentUser(); return true } catch { this.user = null; return false } finally { this.ready = true } },
    async signIn(form: LoginForm) { this.user = await login(form); this.ready = true },
    async signOut() {
      // 先清理前端身份，避免退出请求或浏览器缓存期间继续显示旧用户页面。
      this.user = null
      this.ready = true
      sessionStorage.removeItem('mes-auth')
      await logout().catch(() => undefined)
    },
  },
})
