<script setup lang="ts">
import { computed, h, onMounted, ref } from 'vue'
import UButton from '@nuxt/ui/components/Button.vue'
import type { TableColumn } from '@nuxt/ui'
import { listLogs, type SystemLog } from '@/api/logs'

type LogSortField = 'createdAt' | 'level' | 'module' | 'action' | 'message'
const logs = ref<SystemLog[]>([]); const error = ref(''); const sortBy = ref<LogSortField>('createdAt'); const direction = ref<'asc' | 'desc'>('asc'); const page = ref(1); const pageSize = ref(10)
const pageSizeItems = [{ label: '10 条 / 页', value: 10 }, { label: '20 条 / 页', value: 20 }, { label: '50 条 / 页', value: 50 }]
const sortedLogs = computed(() => [...logs.value].sort((a, b) => { const result = String(a[sortBy.value] ?? '').localeCompare(String(b[sortBy.value] ?? ''), 'zh-CN', { numeric: true }); return direction.value === 'asc' ? result : -result }))
const pagedLogs = computed(() => sortedLogs.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value))
const columns: TableColumn<SystemLog>[] = [{ accessorKey: 'createdAt', header: () => sortHeader('createdAt', '时间') }, { accessorKey: 'level', header: () => sortHeader('level', '级别') }, { accessorKey: 'module', header: () => sortHeader('module', '模块') }, { accessorKey: 'action', header: () => sortHeader('action', '动作') }, { accessorKey: 'message', header: () => sortHeader('message', '内容') }]
function sortHeader(field: LogSortField, label: string): ReturnType<typeof h> { return h(UButton, { variant: 'ghost', color: 'neutral', size: 'xs', label, onClick: () => sortByColumn(field) }) }
function sortByColumn(field: LogSortField): void { if (sortBy.value === field) direction.value = direction.value === 'asc' ? 'desc' : 'asc'; else { sortBy.value = field; direction.value = 'asc' }; page.value = 1 }
async function load(): Promise<void> { try { logs.value = (await listLogs()).items; page.value = 1 } catch { error.value = '系统日志加载失败' } }
function changePageSize(value: number): void { pageSize.value = value; page.value = 1 }
onMounted(() => void load())
</script>

<template><div class="space-y-6"><div><h2 class="text-xl font-bold text-highlighted">系统日志</h2><p class="mt-1 text-sm text-muted">仅系统管理员可查看关键操作审计记录。</p></div><UAlert v-if="error" color="error" variant="soft" :title="error" /><UCard><UTable :data="pagedLogs" :columns="columns" empty="暂无日志"><template #level-cell="{ row }"><UBadge :color="row.original.level === 'ERROR' ? 'error' : 'neutral'" variant="subtle">{{ row.original.level }}</UBadge></template></UTable><div class="mt-5 flex flex-wrap items-center justify-between gap-4 border-t border-default pt-4"><div class="flex items-center gap-2 text-sm text-muted"><span>每页显示</span><USelect :model-value="pageSize" :items="pageSizeItems" value-key="value" class="w-32" @update:model-value="changePageSize" /><span>共 {{ sortedLogs.length }} 条</span></div><UPagination v-if="sortedLogs.length > pageSize" v-model:page="page" :total="sortedLogs.length" :items-per-page="pageSize" /></div></UCard></div></template>
