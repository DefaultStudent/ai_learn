<script setup lang="ts">
export interface NavItem { key: string; label: string; icon: string }
defineProps<{ items: NavItem[]; active: string; collapsed: boolean }>()
const emit = defineEmits<{ select: [key: string]; toggle: [] }>()
</script>

<template>
  <aside :style="{ width: collapsed ? '5.5rem' : '18rem' }" class="mes-sidebar fixed inset-y-0 left-0 z-40 hidden flex-col border-r border-slate-200/70 bg-[#eaf4fb]/90 px-3 py-5 backdrop-blur-xl transition-[width] duration-300 lg:flex">
    <div class="flex items-center gap-3 rounded-2xl bg-white/75 px-3 py-3 shadow-[0_8px_24px_rgb(91_126_151/0.10)]"><UAvatar text="M" :size="collapsed ? 'md' : 'lg'" color="primary" /><div v-if="!collapsed" class="min-w-0"><p class="truncate font-bold tracking-wide text-highlighted">MES FLOW</p><p class="truncate text-xs text-muted">制造执行学习系统</p></div></div>
    <button type="button" class="mes-sidebar-toggle mt-4 self-end rounded-lg p-2 text-slate-500 transition hover:bg-white/80 hover:text-slate-700" :aria-expanded="!collapsed" :aria-label="collapsed ? '展开侧边栏' : '收起侧边栏'" @click="emit('toggle')"><span aria-hidden="true" class="material-symbols-rounded">{{ collapsed ? 'left_panel_open' : 'left_panel_close' }}</span></button>
    <nav class="mt-2 space-y-1"><UButton v-for="item in items" :key="item.key" block :variant="active === item.key ? 'soft' : 'ghost'" :color="active === item.key ? 'primary' : 'neutral'" :square="collapsed" :class="collapsed ? 'justify-center' : 'justify-start'" :aria-label="item.label" @click="emit('select', item.key)"><span class="material-symbols-rounded w-6 text-center text-[21px]">{{ item.icon }}</span><span v-if="!collapsed">{{ item.label }}</span></UButton></nav>
    <div class="mt-auto border-t border-slate-200/70 pt-4 shadow-[0_-1px_3px_rgb(91_126_151/0.06)]"><div class="flex items-center gap-2 rounded-xl bg-emerald-50/80 px-2 py-2 text-sm"><UBadge color="success" variant="subtle" size="sm">正常</UBadge><span v-if="!collapsed" class="text-emerald-800">基础服务运行中</span></div></div>
  </aside>
</template>
