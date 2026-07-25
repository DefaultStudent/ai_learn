<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { createDevice, deleteDevice, listDevices, updateDevice, updateDeviceStatus, type Device, type DeviceSortField } from '@/api/devices'
import ModalDialog from './ModalDialog.vue'
import ToastMessage from './ToastMessage.vue'

type ToastType = 'success' | 'error'
const devices = ref<Device[]>([])
const keyword = ref('')
// 设备管理默认按设备编码升序展示，便于用户按产线/设备编号快速定位。
const sortBy = ref<DeviceSortField>('code')
const direction = ref<'asc' | 'desc'>('asc')
const loading = ref(false)
const form = ref({ code: '', name: '' })
const error = ref('')
const createDialogOpen = ref(false)
const editingDevice = ref<Device | null>(null)
const toast = ref<{ visible: boolean; type: ToastType; message: string }>({ visible: false, type: 'success', message: '' })

const sortedDevices = computed(() => [...devices.value].sort((left, right) => {
  const field = sortBy.value
  const comparison = field === 'id'
    ? left.id - right.id
    : String(left[field]).localeCompare(String(right[field]), 'zh-CN', { numeric: true })
  return direction.value === 'asc' ? comparison : -comparison
}))

async function load() {
  loading.value = true
  error.value = ''
  try { devices.value = (await listDevices({ keyword: keyword.value })).items } catch { error.value = '设备数据加载失败，请检查后端服务' } finally { loading.value = false }
}

function sortByColumn(field: DeviceSortField) {
  if (sortBy.value === field) direction.value = direction.value === 'asc' ? 'desc' : 'asc'
  else { sortBy.value = field; direction.value = 'asc' }
}

function sortIndicator(field: DeviceSortField) { return sortBy.value === field ? (direction.value === 'asc' ? '↑' : '↓') : '↕' }

function openCreateDialog() {
  form.value = { code: '', name: '' }
  error.value = ''
  editingDevice.value = null
  createDialogOpen.value = true
}

function openEditDialog(device: Device) {
  form.value = { code: device.code, name: device.name }
  error.value = ''
  editingDevice.value = device
  createDialogOpen.value = true
}

function closeCreateDialog() {
  createDialogOpen.value = false
  error.value = ''
  editingDevice.value = null
}

function showToast(type: ToastType, message: string) {
  toast.value = { visible: true, type, message }
}

async function addDevice() {
  if (!form.value.code || !form.value.name) {
    showToast('error', '请填写设备编码和设备名称')
    return
  }
  const editing = Boolean(editingDevice.value)
  try {
    if (editingDevice.value) await updateDevice(editingDevice.value.id, form.value)
    else await createDevice(form.value)
    closeCreateDialog()
    await load()
    showToast('success', editing ? '设备修改成功' : '设备添加成功')
  } catch {
    showToast('error', editing ? '设备修改失败，请检查编码是否重复' : '设备添加失败，请检查编码是否重复')
  }
}

async function toggleStatus(device: Device) {
  try { await updateDeviceStatus(device.id, device.status === 'ONLINE' ? 'OFFLINE' : 'ONLINE'); await load() } catch { showToast('error', '状态更新失败') }
}

async function removeDevice(device: Device) {
  if (!window.confirm(`确定删除设备 ${device.name} 吗？`)) return
  try { await deleteDevice(device.id); await load(); showToast('success', '设备删除成功') } catch { showToast('error', '设备删除失败') }
}

onMounted(load)
</script>

<template>
  <div class="space-y-6">
    <ToastMessage :visible="toast.visible" :type="toast.type" :message="toast.message" @close="toast.visible = false" />
    <div class="flex flex-wrap items-end justify-between gap-4">
      <div><h2 class="text-lg font-bold">设备管理</h2><p class="mt-1 text-sm text-slate-400">维护设备主数据并模拟设备状态切换</p></div>
      <div class="flex flex-wrap items-center gap-3">
        <div class="flex gap-2 rounded-xl bg-slate-100 p-1"><input v-model="keyword" @keyup.enter="load" placeholder="搜索编码或名称" class="rounded-lg bg-white px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-indigo-200" /><button type="button" @click="load" class="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700">查询</button></div>
        <button type="button" @click="openCreateDialog" class="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-emerald-700">新增设备</button>
      </div>
    </div>
    <p v-if="error" class="rounded-lg bg-rose-50 px-4 py-3 text-sm text-rose-600">{{ error }}</p>
    <div class="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm"><table class="w-full text-left text-sm"><thead class="bg-slate-50 text-xs uppercase text-slate-400"><tr><th class="px-6 py-4"><button type="button" @click="sortByColumn('code')" class="font-semibold hover:text-indigo-600">编码 {{ sortIndicator('code') }}</button></th><th class="px-6 py-4"><button type="button" @click="sortByColumn('name')" class="font-semibold hover:text-indigo-600">名称 {{ sortIndicator('name') }}</button></th><th class="px-6 py-4"><button type="button" @click="sortByColumn('status')" class="font-semibold hover:text-indigo-600">状态 {{ sortIndicator('status') }}</button></th><th class="px-6 py-4 text-right">操作</th></tr></thead><tbody class="divide-y divide-slate-100"><tr v-if="loading"><td colspan="4" class="px-6 py-10 text-center text-slate-400">加载中...</td></tr><tr v-else-if="sortedDevices.length === 0"><td colspan="4" class="px-6 py-10 text-center text-slate-400">暂无设备数据</td></tr><tr v-for="device in sortedDevices" :key="device.id"><td class="px-6 py-4 font-medium">{{ device.code }}</td><td class="px-6 py-4">{{ device.name }}</td><td class="px-6 py-4"><span :class="device.status === 'ONLINE' ? 'bg-emerald-50 text-emerald-700' : 'bg-slate-100 text-slate-500'" class="rounded-full px-2.5 py-1 text-xs font-medium">{{ device.status }}</span></td><td class="space-x-3 px-6 py-4 text-right"><button type="button" @click="openEditDialog(device)" class="text-indigo-600 hover:text-indigo-800">编辑</button><button type="button" @click="toggleStatus(device)" class="text-slate-600 hover:text-slate-800">切换状态</button><button type="button" @click="removeDevice(device)" class="text-rose-500 hover:text-rose-700">删除</button></td></tr></tbody></table></div>

    <ModalDialog :open="createDialogOpen" :title="editingDevice ? '编辑设备' : '新增设备'" @close="closeCreateDialog">
      <form class="space-y-5" @submit.prevent="addDevice"><p class="text-sm text-slate-500">录入设备主数据后，设备会自动创建为 OFFLINE 状态。</p><label class="block"><span class="mb-1 block text-sm text-slate-600">设备编码</span><input v-model="form.code" required maxlength="64" placeholder="如 A-01" class="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none focus:border-indigo-500" /></label><label class="block"><span class="mb-1 block text-sm text-slate-600">设备名称</span><input v-model="form.name" required maxlength="128" placeholder="如 冲压线 A-01" class="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm outline-none focus:border-indigo-500" /></label><div class="flex justify-end gap-3"><button type="button" @click="closeCreateDialog" class="rounded-lg border border-slate-200 px-4 py-2 text-sm text-slate-600">取消</button><button type="submit" class="rounded-lg bg-emerald-600 px-5 py-2 text-sm font-medium text-white hover:bg-emerald-700">保存设备</button></div></form>
    </ModalDialog>
  </div>
</template>
