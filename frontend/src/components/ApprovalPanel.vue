<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { listApprovals, listMyApprovals, reviewApproval, type ApprovalRequest } from '@/api/approvals'
import ToastMessage from './ToastMessage.vue'

type SortField = 'createdAt' | 'requester' | 'module' | 'action' | 'status'
const props = defineProps<{ reviewable: boolean }>()
const approvals = ref<ApprovalRequest[]>([])
const error = ref('')
const sortBy = ref<SortField>('createdAt')
const direction = ref<'asc' | 'desc'>('asc')
const toast = ref({ visible: false, type: 'success' as 'success' | 'error', message: '' })
const sortedApprovals = computed(() => [...approvals.value].sort((left, right) => {
  const comparison = String(left[sortBy.value] ?? '').localeCompare(String(right[sortBy.value] ?? ''), 'zh-CN', { numeric: true })
  return direction.value === 'asc' ? comparison : -comparison
}))
function sortByColumn(field: SortField): void { if (sortBy.value === field) direction.value = direction.value === 'asc' ? 'desc' : 'asc'; else { sortBy.value = field; direction.value = 'asc' } }
function sortIndicator(field: SortField): string { return sortBy.value === field ? (direction.value === 'asc' ? '↑' : '↓') : '↕' }
async function load(): Promise<void> { try { approvals.value = props.reviewable ? await listApprovals() : await listMyApprovals() } catch { error.value = '审批数据加载失败' } }
async function review(request: ApprovalRequest, approved: boolean): Promise<void> { try { await reviewApproval(request.id, { approved }); await load(); toast.value = { visible: true, type: 'success', message: approved ? '审批已通过' : '审批已驳回' } } catch { toast.value = { visible: true, type: 'error', message: '审批操作失败' } } }
function handleRealtimeApproval(event: Event): void {
  const approvalEvent = (event as CustomEvent<unknown>).detail
  if (approvalEvent && typeof approvalEvent === 'object') void load()
}
onMounted(() => { void load(); window.addEventListener('approval-updated', handleRealtimeApproval) })
onBeforeUnmount(() => window.removeEventListener('approval-updated', handleRealtimeApproval))
</script>

<template>
  <div class="space-y-6">
    <ToastMessage :visible="toast.visible" :type="toast.type" :message="toast.message" @close="toast.visible = false" />
    <div><h2 class="text-lg font-bold">{{ props.reviewable ? '审批管理' : '我的申请' }}</h2><p class="mt-1 text-sm text-slate-400">{{ props.reviewable ? '处理部门管理者提交的设备变更申请' : '查看自己提交的申请及审批状态' }}</p></div>
    <p v-if="error" class="rounded-lg bg-rose-50 px-4 py-3 text-sm text-rose-600">{{ error }}</p>
    <div class="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm"><table class="w-full text-left text-sm"><thead class="bg-slate-50 text-xs text-slate-400"><tr><th class="px-6 py-4"><button type="button" @click="sortByColumn('createdAt')">时间 {{ sortIndicator('createdAt') }}</button></th><th v-if="props.reviewable" class="px-6 py-4"><button type="button" @click="sortByColumn('requester')">申请人 {{ sortIndicator('requester') }}</button></th><th class="px-6 py-4"><button type="button" @click="sortByColumn('module')">模块 {{ sortIndicator('module') }}</button></th><th class="px-6 py-4"><button type="button" @click="sortByColumn('action')">操作 {{ sortIndicator('action') }}</button></th><th class="px-6 py-4"><button type="button" @click="sortByColumn('status')">状态 {{ sortIndicator('status') }}</button></th><th v-if="props.reviewable" class="px-6 py-4 text-right">处理</th></tr></thead><tbody class="divide-y divide-slate-100"><tr v-for="request in sortedApprovals" :key="request.id"><td class="px-6 py-4 text-xs text-slate-500">{{ request.createdAt }}</td><td v-if="props.reviewable" class="px-6 py-4">{{ request.requester }}</td><td class="px-6 py-4">{{ request.module }}</td><td class="px-6 py-4">{{ request.action }}</td><td class="px-6 py-4">{{ request.status }}</td><td v-if="props.reviewable" class="space-x-3 px-6 py-4 text-right"><template v-if="request.status === 'PENDING'"><button type="button" @click="review(request, true)" class="text-emerald-600">批准</button><button type="button" @click="review(request, false)" class="text-rose-500">驳回</button></template><span v-else class="text-xs text-slate-400">已处理</span></td></tr><tr v-if="sortedApprovals.length === 0"><td :colspan="props.reviewable ? 6 : 5" class="px-6 py-10 text-center text-slate-400">暂无申请</td></tr></tbody></table></div>
  </div>
</template>
