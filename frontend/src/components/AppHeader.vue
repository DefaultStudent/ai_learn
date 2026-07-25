<script setup lang="ts">
import { computed } from 'vue'
import type { CurrentUser } from '@/api/auth'
const props = defineProps<{ user: CurrentUser; title: string }>()
const emit = defineEmits<{ logout: [] }>()
const roleLabels: Record<string, string> = { SYSTEM_ADMIN: '系统管理员', ADMIN: '管理员', PRODUCTION_MANAGER: '生产部门管理者', DEVICE_MANAGER: '设备部门管理者', PRODUCTION_USER: '生产部门用户', DEVICE_USER: '设备部门用户' }
const avatarText = computed(() => (props.user.displayName || props.user.username).slice(0, 1).toUpperCase())
const roleLabel = computed(() => roleLabels[props.user.role] ?? '用户')
const menuItems = computed(() => [[{ label: props.user.displayName, disabled: true }, { label: '@' + props.user.username, disabled: true }, { label: roleLabel.value, disabled: true }], [{ label: '退出系统', color: 'error' as const, onSelect: () => emit('logout') }]])
</script>

<template>
  <header class="mes-header flex min-h-20 items-center justify-between border-b border-sky-200/75 px-5 py-3 shadow-[0_8px_24px_rgb(91_126_151/0.10)] backdrop-blur-xl sm:px-8">
    <div class="min-w-0"><p class="text-xs font-medium uppercase tracking-[0.18em] text-slate-500">MES FLOW / OPERATIONS</p><h1 class="mt-1 truncate text-xl font-semibold tracking-tight text-slate-800">{{ title }}</h1></div>
    <div class="flex items-center gap-2 sm:gap-4"><div class="hidden items-center gap-2 rounded-full bg-white/55 px-3 py-2 text-xs text-slate-500 md:flex"><span class="h-2 w-2 rounded-full bg-emerald-500 shadow-[0_0_0_4px_rgb(16_185_129/0.12)]" />系统运行正常</div><UButton variant="ghost" color="neutral" class="hidden items-center gap-2 sm:inline-flex"><span class="material-symbols-rounded text-[18px]">help</span>帮助中心</UButton><UDropdownMenu :items="menuItems" :content="{ align: 'end', sideOffset: 8 }"><UButton variant="ghost" color="neutral" class="flex items-center gap-2 rounded-xl px-2 py-1.5" aria-label="打开用户菜单"><UAvatar :text="avatarText" size="sm" color="primary" /><span class="hidden max-w-32 truncate text-sm font-medium text-slate-700 md:inline">{{ user.displayName }}</span><span aria-hidden="true" class="material-symbols-rounded text-slate-400">expand_more</span></UButton></UDropdownMenu></div>
  </header>
</template>
