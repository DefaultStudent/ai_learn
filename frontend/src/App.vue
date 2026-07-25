<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { getDashboardSummary, type DashboardSummary } from './api/dashboard'
import { listApprovals, type ApprovalRequest } from './api/approvals'
import { getApprovalRealtimeToken } from './api/realtime'
import { useAuthStore } from './stores/auth'
import AppHeader from './components/AppHeader.vue'
import AppSidebar, { type NavItem } from './components/AppSidebar.vue'
import ApprovalPanel from './components/ApprovalPanel.vue'
import DashboardOverview from './components/DashboardOverview.vue'
import DevicePanel from './components/DevicePanel.vue'
import LogPanel from './components/LogPanel.vue'
import LoginPanel from './components/LoginPanel.vue'
import ModalDialog from './components/ModalDialog.vue'
import UserPanel from './components/UserPanel.vue'
import { connectApprovalRealtime, type ApprovalRealtimeEvent } from './realtime/approvalRealtime'

const auth = useAuthStore()
const sessionReady = ref(false)
const active = ref('dashboard')
const loading = ref(true)
const summary = ref<DashboardSummary>({ activeOrders: 0, onlineDevices: 0, todayOutput: 0, qualityRate: 0, devices: [] })
const pendingApprovals = ref<ApprovalRequest[]>([])
const approvalNoticeOpen = ref(false)
const knownPendingApprovalIds = ref<Set<number>>(new Set())
const lastApprovalEvent = ref<ApprovalRealtimeEvent | null>(null)
let closeApprovalRealtime: (() => void) | undefined

const nav = computed<NavItem[]>(() => {
  const items: NavItem[] = [{ key: 'dashboard', label: '生产总览', icon: '▦' }]
  if (auth.canViewProduction) items.push({ key: 'orders', label: '生产工单', icon: '▤' })
  if (auth.canViewDevices) items.push({ key: 'devices', label: '设备管理', icon: '◉' })
  if (auth.isSystemAdmin) { items.push({ key: 'users', label: '用户管理', icon: '♙' }); items.push({ key: 'logs', label: '系统日志', icon: '≡' }) }
  if (auth.user?.role === 'SYSTEM_ADMIN' || auth.user?.role === 'ADMIN') items.push({ key: 'approvals', label: '审批管理', icon: '✓' })
  if (auth.user?.role === 'PRODUCTION_MANAGER' || auth.user?.role === 'DEVICE_MANAGER') items.push({ key: 'my-approvals', label: '我的申请', icon: '◷' })
  return items
})
const activeTitle = computed(() => nav.value.find((item) => item.key === active.value)?.label ?? '生产总览')

function resetView(): void { active.value = 'dashboard'; loading.value = false; summary.value = { activeOrders: 0, onlineDevices: 0, todayOutput: 0, qualityRate: 0, devices: [] } }
async function signOut(): Promise<void> { resetView(); await auth.signOut() }
async function loadSummary(): Promise<void> { loading.value = true; try { summary.value = await getDashboardSummary() } finally { loading.value = false } }
async function loadPendingApprovals(showExisting: boolean = false): Promise<void> {
  const canReview = auth.user?.role === 'SYSTEM_ADMIN' || auth.user?.role === 'ADMIN'
  if (!canReview) { pendingApprovals.value = []; knownPendingApprovalIds.value = new Set(); approvalNoticeOpen.value = false; return }
  try {
    const currentPending = (await listApprovals()).filter((request) => request.status === 'PENDING')
    const newRequestArrived = currentPending.some((request) => !knownPendingApprovalIds.value.has(request.id))
    pendingApprovals.value = currentPending
    knownPendingApprovalIds.value = new Set(currentPending.map((request) => request.id))
    if (showExisting || newRequestArrived) approvalNoticeOpen.value = currentPending.length > 0
  }
  catch { pendingApprovals.value = []; approvalNoticeOpen.value = false }
}
function handleApprovalEvent(event: ApprovalRealtimeEvent): void {
  lastApprovalEvent.value = event
  window.dispatchEvent(new CustomEvent<ApprovalRealtimeEvent>('approval-updated', { detail: event }))
  if (auth.user?.role !== 'SYSTEM_ADMIN' && auth.user?.role !== 'ADMIN') return
  if (event.status !== 'PENDING' || pendingApprovals.value.some((request) => request.id === event.id)) return
  const request: ApprovalRequest = { id: event.id, requester: event.requester, module: event.module, action: event.action, payload: '', status: 'PENDING', reviewer: null, reviewRemark: null, createdAt: new Date().toISOString(), reviewedAt: null }
  pendingApprovals.value = [...pendingApprovals.value, request]
  knownPendingApprovalIds.value.add(event.id)
  approvalNoticeOpen.value = true
}
async function startApprovalRealtime(): Promise<void> {
  if (!auth.user) return
  try {
    const token = await getApprovalRealtimeToken()
    if (!auth.user) return
    closeApprovalRealtime = connectApprovalRealtime(token, handleApprovalEvent)
  } catch { closeApprovalRealtime = undefined }
}
function openApprovalPage(): void { approvalNoticeOpen.value = false; active.value = 'approvals' }
function handlePageShow(event: PageTransitionEvent): void { if (event.persisted) void signOut() }

onMounted(async () => {
  window.addEventListener('pageshow', handlePageShow)
  closeApprovalRealtime = undefined
  await signOut()
  sessionReady.value = true
})
onBeforeUnmount(() => { window.removeEventListener('pageshow', handlePageShow); closeApprovalRealtime?.() })
watch(active, (value) => { if (value === 'dashboard' && auth.user) void loadSummary() })
watch(() => auth.user?.id, (userId) => {
  closeApprovalRealtime?.()
  closeApprovalRealtime = undefined
  if (userId) {
    void loadSummary()
    void loadPendingApprovals(true)
    void startApprovalRealtime()
  } else { resetView(); pendingApprovals.value = []; knownPendingApprovalIds.value = new Set(); approvalNoticeOpen.value = false }
})
</script>

<template>
  <div v-if="!sessionReady" class="grid min-h-screen place-items-center bg-slate-100 text-sm text-slate-400">正在准备登录环境...</div>
  <LoginPanel v-else-if="!auth.user" />
  <div v-else class="min-h-screen bg-slate-50">
    <AppSidebar :items="nav" :active="active" @select="active = $event" />
    <main class="lg:ml-64"><AppHeader :user="auth.user" :title="activeTitle" @logout="signOut" /><section class="space-y-7 p-6 lg:p-10"><DevicePanel v-if="active === 'devices'" :can-manage="auth.canManageDevices" :can-propose="auth.canProposeDeviceChanges" /><UserPanel v-else-if="active === 'users' && auth.isSystemAdmin" /><LogPanel v-else-if="active === 'logs' && auth.isSystemAdmin" /><ApprovalPanel v-else-if="active === 'approvals' && (auth.isSystemAdmin || auth.user.role === 'ADMIN')" :reviewable="true" /><ApprovalPanel v-else-if="active === 'my-approvals' && (auth.user.role === 'PRODUCTION_MANAGER' || auth.user.role === 'DEVICE_MANAGER')" :reviewable="false" /><div v-else-if="active !== 'dashboard'" class="rounded-2xl border border-dashed border-slate-300 bg-white p-10 text-center text-slate-500">{{ activeTitle }}模块正在建设中。</div><DashboardOverview v-else :summary="summary" :loading="loading" /></section></main>
  </div>
  <ModalDialog :open="approvalNoticeOpen" title="待处理审批提醒" @close="approvalNoticeOpen = false"><div class="space-y-4"><p class="text-sm text-slate-600">当前有 {{ pendingApprovals.length }} 条申请等待处理。</p><div class="max-h-56 space-y-2 overflow-y-auto"><div v-for="request in pendingApprovals" :key="request.id" class="rounded-lg bg-slate-50 px-3 py-2 text-sm"><span class="font-medium">{{ request.requester }}</span><span class="mx-2 text-slate-400">·</span><span>{{ request.module }} / {{ request.action }}</span></div></div><div class="flex justify-end gap-3"><button type="button" @click="approvalNoticeOpen = false" class="rounded-lg border px-4 py-2 text-sm">稍后处理</button><button type="button" @click="openApprovalPage" class="rounded-lg bg-indigo-600 px-4 py-2 text-sm text-white">进入审批管理</button></div></div></ModalDialog>
</template>
