<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { listLogs, type SystemLog } from '@/api/logs'

type LogSortField = 'createdAt' | 'level' | 'module' | 'action' | 'message'

const logs = ref<SystemLog[]>([])
const error = ref('')
const sortBy = ref<LogSortField>('createdAt')
const direction = ref<'asc' | 'desc'>('asc')

const sortedLogs = computed(() => [...logs.value].sort((left, right) => {
  const leftValue = String(left[sortBy.value] ?? '')
  const rightValue = String(right[sortBy.value] ?? '')
  const comparison = leftValue.localeCompare(rightValue, 'zh-CN', { numeric: true })
  return direction.value === 'asc' ? comparison : -comparison
}))

function sortByColumn(field: LogSortField): void {
  if (sortBy.value === field) {
    direction.value = direction.value === 'asc' ? 'desc' : 'asc'
    return
  }
  sortBy.value = field
  direction.value = 'asc'
}

function sortIndicator(field: LogSortField): string {
  return sortBy.value === field ? (direction.value === 'asc' ? '↑' : '↓') : '↕'
}

async function load(): Promise<void> {
  try {
    logs.value = (await listLogs()).items
  } catch {
    error.value = '系统日志加载失败'
  }
}

onMounted(load)
</script>

<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-lg font-bold">系统日志</h2>
      <p class="mt-1 text-sm text-slate-400">仅系统管理员可查看关键操作审计记录</p>
    </div>
    <p v-if="error" class="rounded-lg bg-rose-50 px-4 py-3 text-sm text-rose-600">{{ error }}</p>
    <div class="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <table class="w-full text-left text-sm">
        <thead class="bg-slate-50 text-xs text-slate-400">
          <tr>
            <th class="px-6 py-4"><button type="button" @click="sortByColumn('createdAt')">时间 {{ sortIndicator('createdAt') }}</button></th>
            <th class="px-6 py-4"><button type="button" @click="sortByColumn('level')">级别 {{ sortIndicator('level') }}</button></th>
            <th class="px-6 py-4"><button type="button" @click="sortByColumn('module')">模块 {{ sortIndicator('module') }}</button></th>
            <th class="px-6 py-4"><button type="button" @click="sortByColumn('action')">动作 {{ sortIndicator('action') }}</button></th>
            <th class="px-6 py-4"><button type="button" @click="sortByColumn('message')">内容 {{ sortIndicator('message') }}</button></th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
          <tr v-for="log in sortedLogs" :key="log.id">
            <td class="px-6 py-4 text-xs text-slate-500">{{ log.createdAt }}</td>
            <td class="px-6 py-4">{{ log.level }}</td>
            <td class="px-6 py-4">{{ log.module }}</td>
            <td class="px-6 py-4">{{ log.action }}</td>
            <td class="px-6 py-4">{{ log.message }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
