<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import type { CurrentUser } from '@/api/auth'
defineProps<{ user: CurrentUser; title: string }>()
const emit = defineEmits<{ logout: [] }>()
const menuRoot = ref<HTMLDetailsElement | null>(null)
const roleLabels: Record<string, string> = { SYSTEM_ADMIN: '系统管理员', ADMIN: '管理员', PRODUCTION_MANAGER: '生产部门管理者', DEVICE_MANAGER: '设备部门管理者', PRODUCTION_USER: '生产部门普通用户', DEVICE_USER: '设备部门普通用户' }
function initial(user: CurrentUser): string { return (user.displayName || user.username).slice(0, 1).toUpperCase() }
function roleLabel(role: string): string { return roleLabels[role] ?? '用户' }
function closeWhenBlurred(event: MouseEvent): void { if (menuRoot.value && !menuRoot.value.contains(event.target as Node)) menuRoot.value.open = false }
onMounted(() => document.addEventListener('click', closeWhenBlurred))
onBeforeUnmount(() => document.removeEventListener('click', closeWhenBlurred))
</script>

<template>
  <header class="flex h-20 items-center justify-between border-b border-slate-200 bg-white px-6 lg:px-10"><div><p class="text-sm text-slate-400">制造执行 / {{ title }}</p><h1 class="text-xl font-bold">{{ title }}</h1></div><div class="flex items-center gap-3"><button type="button" class="rounded-lg border border-slate-200 px-3 py-2 text-sm text-slate-500">帮助文档</button><details ref="menuRoot" class="relative"><summary class="grid h-10 w-10 cursor-pointer list-none place-items-center rounded-full bg-indigo-100 text-sm font-bold text-indigo-700">{{ initial(user) }}</summary><div class="absolute right-0 top-12 z-30 w-64 rounded-xl border border-slate-200 bg-white p-4 shadow-lg"><p class="font-semibold text-slate-800">{{ user.displayName }}</p><p class="mt-1 text-xs text-slate-400">@{{ user.username }}</p><p class="mt-3 rounded-lg bg-indigo-50 px-3 py-2 text-xs text-indigo-700">{{ roleLabel(user.role) }}</p><button type="button" class="mt-3 w-full rounded-lg bg-rose-50 px-3 py-2 text-left text-sm text-rose-600 hover:bg-rose-100" @click="emit('logout')">退出系统</button></div></details></div></header>
</template>
