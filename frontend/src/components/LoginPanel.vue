<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
const auth = useAuthStore()
const form = ref({ username: '', password: '' })
const error = ref('')
const loading = ref(false)
async function submit(): Promise<void> { error.value = ''; loading.value = true; try { await auth.signIn(form.value) } catch { error.value = '用户名或密码错误，请重试' } finally { loading.value = false } }
</script>

<template>
  <main class="mes-login min-h-screen px-5 py-8 sm:px-8"><div class="mx-auto grid min-h-[calc(100vh-4rem)] max-w-6xl items-center gap-12 lg:grid-cols-[1.1fr_0.9fr]">
    <section class="hidden lg:block"><div class="mb-8 flex items-center gap-3"><UAvatar text="M" size="lg" color="primary" /><span class="text-lg font-bold tracking-[0.22em] text-slate-800">MES FLOW</span></div><p class="mb-5 text-sm font-semibold uppercase tracking-[0.22em] text-primary">MANUFACTURING OPERATIONS</p><h1 class="max-w-xl text-5xl font-semibold leading-[1.08] tracking-tight text-slate-800">让生产现场的每一次决策，都更清晰。</h1><p class="mt-6 max-w-lg text-base leading-8 text-slate-500">统一查看生产、设备和审批状态，在一个安静而高效的工作台中完成日常运营。</p><div class="mt-10 grid max-w-lg grid-cols-3 gap-3"><div class="rounded-2xl bg-white/62 p-4 shadow-[0_12px_28px_rgb(72_105_127/0.08)]"><p class="text-2xl font-semibold text-slate-800">24/7</p><p class="mt-1 text-xs text-slate-500">实时可见</p></div><div class="rounded-2xl bg-white/62 p-4 shadow-[0_12px_28px_rgb(72_105_127/0.08)]"><p class="text-2xl font-semibold text-slate-800">1 个</p><p class="mt-1 text-xs text-slate-500">统一入口</p></div><div class="rounded-2xl bg-white/62 p-4 shadow-[0_12px_28px_rgb(72_105_127/0.08)]"><p class="text-2xl font-semibold text-slate-800">清晰</p><p class="mt-1 text-xs text-slate-500">协作体验</p></div></div></section>
    <UCard class="mes-card mx-auto w-full max-w-md" :ui="{ body: 'p-7 sm:p-9' }"><div class="mb-8 text-center lg:text-left"><div class="mb-5 flex justify-center lg:hidden"><UAvatar text="M" size="xl" color="primary" /></div><p class="text-sm font-medium text-primary">欢迎回来</p><h2 class="mt-2 text-2xl font-semibold text-slate-800">登录工作台</h2><p class="mt-2 text-sm text-slate-500">使用你的账号继续管理制造执行流程</p></div><form class="space-y-5" @submit.prevent="submit"><UFormField label="用户名" required><UInput v-model="form.username" class="w-full" autocomplete="username" placeholder="输入用户名" size="lg" /></UFormField><UFormField label="密码" required><UInput v-model="form.password" class="w-full" type="password" autocomplete="current-password" placeholder="输入密码" size="lg" /></UFormField><UAlert v-if="error" color="error" variant="soft" :title="error" /><UButton type="submit" block size="lg" color="primary" :loading="loading">{{ loading ? '正在登录…' : '进入系统' }}</UButton></form><p class="mt-7 text-center text-xs leading-5 text-slate-400">学习环境账号请参考项目 README</p></UCard>
  </div></main>
</template>
