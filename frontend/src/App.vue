<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import AppSidebar, { type NavItem } from '@/components/AppSidebar.vue'
import LoginPanel from '@/components/LoginPanel.vue'
import ModalDialog from '@/components/ModalDialog.vue'
import { useApprovalRealtime } from '@/composables/useApprovalRealtime'
import { useSession } from '@/composables/useSession'

const route = useRoute()
const router = useRouter()
const { auth, sessionReady, initialize, signOut } = useSession()
const { pendingApprovals, approvalNoticeOpen, loadPendingApprovals, start: startApprovalRealtime, openApprovalPage } = useApprovalRealtime()
const sidebarCollapsed = ref(false)
const nav = computed<NavItem[]>(() => {
  const items: NavItem[] = [{ key: 'dashboard', label: '生产总览', icon: 'dashboard' }]
  if (auth.canViewProduction) items.push({ key: 'orders', label: '生产工单', icon: 'assignment' })
  if (auth.canViewDevices) items.push({ key: 'devices', label: '设备管理', icon: 'precision_manufacturing' })
  if (auth.isSystemAdmin) {
    items.push({ key: 'users', label: '用户管理', icon: 'group' })
    items.push({ key: 'logs', label: '系统日志', icon: 'description' })
  }
  if (auth.canReviewApprovals) items.push({ key: 'approvals', label: '审批管理', icon: 'fact_check' })
  if (auth.canViewMyApprovals) items.push({ key: 'my-approvals', label: '我的申请', icon: 'inbox' })
  return items
})
const activeKey = computed(() => String(route.name ?? 'dashboard'))
const activeTitle = computed(() => nav.value.find((item) => item.key === activeKey.value)?.label ?? '生产总览')
async function selectPage(key: string): Promise<void> { await router.push({ name: key }) }
onMounted(() => void initialize())
watch(() => auth.user?.id, (userId) => {
  if (!userId) { if (route.name !== 'login') void router.push({ name: 'login' }); return }
  if (route.name === 'login') void router.push({ name: 'dashboard' })
  void loadPendingApprovals(true)
  void startApprovalRealtime()
})
</script>

<template>
  <UApp>
    <div v-if="!sessionReady" class="grid min-h-screen place-items-center bg-elevated text-sm text-muted">正在准备登录环境...</div>
    <LoginPanel v-else-if="!auth.user" />
    <div v-else class="mes-workspace min-h-screen">
      <AppSidebar :items="nav" :active="activeKey" :collapsed="sidebarCollapsed" @select="selectPage" @toggle="sidebarCollapsed = !sidebarCollapsed" />
      <main :class="['mes-main min-h-screen transition-[margin] duration-300', sidebarCollapsed ? 'lg:ml-[5.5rem]' : 'lg:ml-72']">
        <AppHeader :user="auth.user" :title="activeTitle" @logout="signOut" />
        <UContainer class="py-8"><RouterView /></UContainer>
      </main>
      <ModalDialog :open="approvalNoticeOpen" title="待处理审批提醒" @close="approvalNoticeOpen = false">
        <div class="space-y-4"><p class="text-sm text-muted">当前有 {{ pendingApprovals.length }} 条申请等待处理。</p><div class="max-h-56 space-y-2 overflow-y-auto"><UCard v-for="request in pendingApprovals" :key="request.id" class="mes-card" variant="subtle"><span class="font-medium">{{ request.requester }}</span><span class="mx-2 text-muted">/</span><span>{{ request.module }} / {{ request.action }}</span></UCard></div><div class="flex justify-end gap-3"><UButton type="button" variant="outline" color="neutral" @click="approvalNoticeOpen = false">稍后处理</UButton><UButton type="button" variant="solid" @click="openApprovalPage">进入审批管理</UButton></div></div>
      </ModalDialog>
    </div>
  </UApp>
</template>
