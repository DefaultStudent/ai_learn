import { defineStore } from 'pinia'
import { currentUser, login, logout, type CurrentUser, type LoginForm } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({ user: null as CurrentUser | null, ready: false }),
  getters: {
    hasPermission: (state) => (permission: string): boolean => state.user?.permissions.includes(permission) ?? false,
    isSystemAdmin: (state): boolean => state.user?.permissions.includes('USER_MANAGE') === true && state.user?.permissions.includes('SYSTEM_LOG_VIEW') === true,
    canManageDevices: (state): boolean => state.user?.permissions.includes('DEVICE_MANAGE') === true,
    canProposeDeviceChanges: (state): boolean => state.user?.permissions.includes('DEVICE_PROPOSE') === true,
    canViewDevices: (state): boolean => state.user?.permissions.includes('DEVICE_VIEW') === true,
    canViewProduction: (state): boolean => state.user?.permissions.includes('PRODUCTION_VIEW') === true,
    canReviewApprovals: (state): boolean => state.user?.permissions.includes('APPROVAL_REVIEW') === true,
    canSubmitApprovals: (state): boolean => state.user?.permissions.includes('APPROVAL_SUBMIT') === true,
    canViewMyApprovals: (state): boolean => state.user?.permissions.includes('APPROVAL_MINE') === true,
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
