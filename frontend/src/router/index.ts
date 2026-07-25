import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: RouteRecordRaw[] = [
  { path: '/login', name: 'login', component: () => import('@/components/LoginPanel.vue'), meta: { public: true } },
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', name: 'dashboard', component: () => import('@/views/DashboardView.vue') },
  { path: '/devices', name: 'devices', component: () => import('@/views/DeviceView.vue'), meta: { permission: 'DEVICE_VIEW' } },
  { path: '/approvals', name: 'approvals', component: () => import('@/views/ApprovalView.vue'), props: { reviewable: true }, meta: { permission: 'APPROVAL_REVIEW' } },
  { path: '/my-approvals', name: 'my-approvals', component: () => import('@/views/ApprovalView.vue'), props: { reviewable: false }, meta: { permission: 'APPROVAL_MINE' } },
  { path: '/users', name: 'users', component: () => import('@/components/UserPanel.vue'), meta: { permission: 'USER_MANAGE' } },
  { path: '/logs', name: 'logs', component: () => import('@/components/LogPanel.vue'), meta: { permission: 'SYSTEM_LOG_VIEW' } },
  { path: '/orders', name: 'orders', component: () => import('@/views/DashboardView.vue'), meta: { permission: 'PRODUCTION_VIEW' } },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.public === true) return auth.user ? { name: 'dashboard' } : true
  if (!auth.user) return { name: 'login' }
  const permission = to.meta.permission
  if (typeof permission === 'string' && !auth.hasPermission(permission)) return { name: 'dashboard' }
  return true
})

export default router
