<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { createDevice, deleteDevice, listDevices, updateDeviceStatus, type Device } from '@/api/devices'

// 设备管理页面：提供学习用的查询、创建、状态切换和删除操作。
const devices = ref<Device[]>([])
const keyword = ref('')
const loading = ref(false)
const form = ref({ code: '', name: '' })
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try { devices.value = (await listDevices({ keyword: keyword.value })).items } catch { error.value = '设备数据加载失败，请检查后端服务' } finally { loading.value = false }
}
async function addDevice() {
  if (!form.value.code || !form.value.name) return
  try { await createDevice(form.value); form.value = { code: '', name: '' }; await load() } catch { error.value = '设备创建失败，请检查编码是否重复' }
}
async function toggleStatus(device: Device) {
  try { await updateDeviceStatus(device.id, device.status === 'ONLINE' ? 'OFFLINE' : 'ONLINE'); await load() } catch { error.value = '状态更新失败' }
}
async function removeDevice(device: Device) {
  if (!window.confirm(`确定删除设备 ${device.name} 吗？`)) return
  try { await deleteDevice(device.id); await load() } catch { error.value = '设备删除失败' }
}
onMounted(load)
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-wrap items-end justify-between gap-4">
      <div>
        <h2 class="text-lg font-bold">设备管理</h2>
        <p class="mt-1 text-sm text-slate-400">维护设备主数据并模拟设备状态切换</p>
      </div>
      <div class="flex gap-2"><input v-model="keyword" @keyup.enter="load" placeholder="搜索编码或名称"
          class="rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none focus:border-indigo-500" /><button
          @click="load"
          class="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700">查询</button>
      </div>
    </div>
    <p v-if="error" class="rounded-lg bg-rose-50 px-4 py-3 text-sm text-rose-600">{{ error }}</p>
    <div class="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <h3 class="font-semibold">新增设备</h3>
      <div class="mt-4 flex flex-wrap gap-3"><input v-model="form.code" placeholder="设备编码，如 A-01"
          class="rounded-lg border border-slate-200 px-3 py-2 text-sm" /><input v-model="form.name" placeholder="设备名称"
          class="rounded-lg border border-slate-200 px-3 py-2 text-sm" /><button @click="addDevice"
          class="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white hover:bg-emerald-700">保存设备</button>
      </div>
    </div>
    <div class="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <table class="w-full text-left text-sm">
        <thead class="bg-slate-50 text-xs uppercase text-slate-400">
          <tr>
            <th class="px-6 py-4">编码</th>
            <th class="px-6 py-4">名称</th>
            <th class="px-6 py-4">状态</th>
            <th class="px-6 py-4 text-right">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
          <tr v-if="loading">
            <td colspan="4" class="px-6 py-10 text-center text-slate-400">加载中...</td>
          </tr>
          <tr v-else-if="devices.length === 0">
            <td colspan="4" class="px-6 py-10 text-center text-slate-400">暂无设备数据</td>
          </tr>
          <tr v-for="device in devices" :key="device.id">
            <td class="px-6 py-4 font-medium">{{ device.code }}</td>
            <td class="px-6 py-4">{{ device.name }}</td>
            <td class="px-6 py-4"><span
                :class="device.status === 'ONLINE' ? 'bg-emerald-50 text-emerald-700' : 'bg-slate-100 text-slate-500'"
                class="rounded-full px-2.5 py-1 text-xs font-medium">{{ device.status }}</span></td>
            <td class="space-x-3 px-6 py-4 text-right"><button @click="toggleStatus(device)"
                class="text-indigo-600 hover:text-indigo-800">切换状态</button><button @click="removeDevice(device)"
                class="text-rose-500 hover:text-rose-700">删除</button></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
