<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const form = ref({ username: '', password: '' })
const error = ref('')
const loading = ref(false)
async function submit() {
  error.value = ''; loading.value = true
  try { await auth.signIn(form.value) } catch { error.value = '用户名或密码错误' } finally { loading.value = false }
}
</script>

<template>
  <main class="grid min-h-screen place-items-center bg-slate-100 p-6"><section class="w-full max-w-md rounded-2xl bg-white p-8 shadow-xl"><div class="mb-8 text-center"><div class="mx-auto grid h-12 w-12 place-items-center rounded-xl bg-indigo-600 text-xl font-bold text-white">M</div><h1 class="mt-4 text-2xl font-bold">MES FLOW</h1><p class="mt-1 text-sm text-slate-400">请登录后进入制造执行系统</p></div><form class="space-y-4" @submit.prevent="submit"><label class="block"><span class="mb-1 block text-sm text-slate-600">用户名</span><input v-model="form.username" required autocomplete="username" class="w-full rounded-lg border border-slate-200 px-3 py-2 outline-none focus:border-indigo-500" /></label><label class="block"><span class="mb-1 block text-sm text-slate-600">密码</span><input v-model="form.password" required type="password" autocomplete="current-password" class="w-full rounded-lg border border-slate-200 px-3 py-2 outline-none focus:border-indigo-500" /></label><p v-if="error" class="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{{ error }}</p><button type="submit" :disabled="loading" class="w-full rounded-lg bg-indigo-600 py-3 font-medium text-white hover:bg-indigo-700 disabled:opacity-50">{{ loading ? '登录中...' : '登录系统' }}</button></form><p class="mt-6 text-center text-xs text-slate-400">学习环境默认账号见 README</p></section></main>
</template>
