<script setup lang="ts">
import { computed, h, onBeforeUnmount, onMounted, ref } from 'vue'
import UButton from '@nuxt/ui/components/Button.vue'
import type { TableColumn } from '@nuxt/ui'
import { listApprovals, listMyApprovals, reviewApproval, type ApprovalRequest } from '@/api/approvals'
import ToastMessage from './ToastMessage.vue'

type SortField = 'createdAt' | 'requester' | 'module' | 'action' | 'status'
const props = defineProps<{ reviewable: boolean }>()
const approvals = ref<ApprovalRequest[]>([]); const error = ref(''); const sortBy = ref<SortField>('createdAt'); const direction = ref<'asc' | 'desc'>('asc'); const page = ref(1); const pageSize = ref(10); const toast = ref({ visible: false, type: 'success' as 'success' | 'error', message: '' })
const pageSizeItems = [{ label: '10 条 / 页', value: 10 }, { label: '20 条 / 页', value: 20 }, { label: '50 条 / 页', value: 50 }]
const sortedApprovals = computed(() => [...approvals.value].sort((a, b) => { const result = String(a[sortBy.value] ?? '').localeCompare(String(b[sortBy.value] ?? ''), 'zh-CN', { numeric: true }); return direction.value === 'asc' ? result : -result }))
const pagedApprovals = computed(() => sortedApprovals.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value))
function sortHeader(field: SortField, label: string): ReturnType<typeof h> { return h(UButton, { variant: 'ghost', color: 'neutral', size: 'xs', label, onClick: () => sortByColumn(field) }) }
const columns = computed<TableColumn<ApprovalRequest>[]>(() => [{ accessorKey: 'createdAt', header: () => sortHeader('createdAt', '时间') }, ...(props.reviewable ? [{ accessorKey: 'requester', header: () => sortHeader('requester', '申请人') } as TableColumn<ApprovalRequest>] : []), { accessorKey: 'module', header: '模块' }, { accessorKey: 'action', header: '操作' }, { accessorKey: 'status', header: '状态' }, ...(props.reviewable ? [{ id: 'actions', header: '处理' } as TableColumn<ApprovalRequest>] : [])])
function sortByColumn(field: SortField): void { if (sortBy.value === field) direction.value = direction.value === 'asc' ? 'desc' : 'asc'; else { sortBy.value = field; direction.value = 'asc' }; page.value = 1 }
async function load(): Promise<void> { try { approvals.value = props.reviewable ? await listApprovals() : await listMyApprovals(); page.value = 1 } catch { error.value = '审批数据加载失败' } }
async function review(request: ApprovalRequest, approved: boolean): Promise<void> { try { await reviewApproval(request.id, { approved }); await load(); toast.value = { visible: true, type: 'success', message: approved ? '审批已通过' : '审批已驳回' } } catch { toast.value = { visible: true, type: 'error', message: '审批操作失败' } } }
function handleRealtimeApproval(): void { void load() }
function changePageSize(value: number): void { pageSize.value = value; page.value = 1 }
onMounted(() => { void load(); window.addEventListener('approval-updated', handleRealtimeApproval) }); onBeforeUnmount(() => window.removeEventListener('approval-updated', handleRealtimeApproval))
</script>

<template><div class="space-y-6"><ToastMessage :visible="toast.visible" :type="toast.type" :message="toast.message" @close="toast.visible = false" /><div><h2 class="text-xl font-bold text-highlighted">{{ props.reviewable ? '审批管理' : '我的申请' }}</h2><p class="mt-1 text-sm text-muted">{{ props.reviewable ? '处理部门管理者提交的设备变更申请' : '查看自己提交的申请和审批状态' }}</p></div><UAlert v-if="error" color="error" variant="soft" :title="error" /><UCard><UTable :data="pagedApprovals" :columns="columns" empty="暂无申请"><template #status-cell="{ row }"><UBadge :color="row.original.status === 'PENDING' ? 'warning' : row.original.status === 'APPROVED' ? 'success' : 'error'" variant="subtle">{{ row.original.status }}</UBadge></template><template #actions-cell="{ row }"><div v-if="row.original.status === 'PENDING'" class="flex justify-end gap-2"><UButton size="xs" color="success" variant="solid" @click="review(row.original, true)">批准</UButton><UButton size="xs" color="error" variant="soft" @click="review(row.original, false)">驳回</UButton></div><span v-else class="text-xs text-muted">已处理</span></template></UTable><div class="mt-5 flex flex-wrap items-center justify-between gap-4 border-t border-default pt-4"><div class="flex items-center gap-2 text-sm text-muted"><span>每页显示</span><USelect :model-value="pageSize" :items="pageSizeItems" value-key="value" class="w-32" @update:model-value="changePageSize" /><span>共 {{ sortedApprovals.length }} 条</span></div><UPagination v-if="sortedApprovals.length > pageSize" v-model:page="page" :total="sortedApprovals.length" :items-per-page="pageSize" /></div></UCard></div></template>
