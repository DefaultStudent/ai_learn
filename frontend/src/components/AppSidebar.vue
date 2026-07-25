<script setup lang="ts">
export interface NavItem { key: string; label: string; icon: string }
defineProps<{ items: NavItem[]; active: string }>()
const emit = defineEmits<{ select: [key: string] }>()
</script>

<template>
  <aside class="fixed inset-y-0 left-0 z-10 hidden w-64 border-r border-slate-200 bg-white lg:block">
    <div class="flex h-20 items-center gap-3 border-b border-slate-100 px-7"><div class="grid h-10 w-10 place-items-center rounded-xl bg-indigo-600 font-bold text-white">M</div><div><p class="font-bold tracking-wide">MES FLOW</p><p class="text-xs text-slate-400">学习型制造执行系统</p></div></div>
    <nav class="space-y-2 p-4"><button v-for="item in items" :key="item.key" type="button" @click="emit('select', item.key)" :class="active === item.key ? 'bg-indigo-50 text-indigo-700' : 'text-slate-500 hover:bg-slate-50'" class="flex w-full items-center gap-3 rounded-xl px-4 py-3 text-left text-sm font-medium"><span class="text-lg">{{ item.icon }}</span>{{ item.label }}</button></nav>
    <div class="absolute bottom-0 w-full border-t border-slate-100 p-5"><p class="text-xs text-slate-400">系统状态</p><div class="mt-2 flex items-center gap-2 text-sm"><span class="h-2 w-2 rounded-full bg-emerald-500"></span>基础服务运行正常</div></div>
  </aside>
</template>
