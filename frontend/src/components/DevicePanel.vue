<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { submitApproval } from '@/api/approvals'
import { createDevice, deleteDevice, listDevices, updateDevice, updateDeviceStatus, type Device, type DeviceSortField } from '@/api/devices'
import ModalDialog from './ModalDialog.vue'
import ToastMessage from './ToastMessage.vue'

type ToastType = 'success' | 'error'
type SortDirection = 'asc' | 'desc'
const props = defineProps<{ canManage: boolean; canPropose: boolean }>()
const devices = ref<Device[]>([])
const keyword = ref('')
const sortBy = ref<DeviceSortField>('code')
const direction = ref<SortDirection>('asc')
const loading = ref(false)
const error = ref('')
const dialogOpen = ref(false)
const editingDevice = ref<Device | null>(null)
const form = ref({ code: '', name: '' })
const toast = ref<{ visible: boolean; type: ToastType; message: string }>({ visible: false, type: 'success', message: '' })

const sortedDevices = computed(() => [...devices.value].sort((left, right) => {
  const comparison = sortBy.value === 'id' ? left.id - right.id : String(left[sortBy.value]).localeCompare(String(right[sortBy.value]), 'zh-CN', { numeric: true })
  return direction.value === 'asc' ? comparison : -comparison
}))

function sortByColumn(field: DeviceSortField): void {
  if (sortBy.value === field) direction.value = direction.value === 'asc' ? 'desc' : 'asc'
  else { sortBy.value = field; direction.value = 'asc' }
}

function sortIndicator(field: DeviceSortField): string { return sortBy.value === field ? (direction.value === 'asc' ? '↑' : '↓') : '↕' }
function showToast(type: ToastType, message: string): void { toast.value = { visible: true, type, message } }

async function load(): Promise<void> {
  loading.value = true
  error.value = ''
  try { devices.value = (await listDevices({ keyword: keyword.value })).items }
  catch { error.value = '设备数据加载失败，请检查后端服务' }
  finally { loading.value = false }
}

function openCreateDialog(): void { editingDevice.value = null; form.value = { code: '', name: '' }; dialogOpen.value = true }
function openEditDialog(device: Device): void { editingDevice.value = device; form.value = { code: device.code, name: device.name }; dialogOpen.value = true }
function closeDialog(): void { dialogOpen.value = false; editingDevice.value = null }
function isProposal(): boolean { return props.canPropose && !props.canManage }

async function saveDevice(): Promise<void> {
  if (!form.value.code || !form.value.name) { showToast('error', '请填写设备编码和设备名称'); return }
  const editing = editingDevice.value !== null
  try {
    if (isProposal()) await submitApproval({ module: 'device', action: editing ? 'UPDATE' : 'CREATE', payload: JSON.stringify({ id: editingDevice.value?.id, ...form.value }) })
    else if (editingDevice.value) await updateDevice(editingDevice.value.id, form.value)
    else await createDevice(form.value)
    closeDialog()
    if (!isProposal()) await load()
    showToast('success', isProposal() ? '变更已提交，等待管理员审批' : (editing ? '设备修改成功' : '设备添加成功'))
  } catch { showToast('error', '操作失败，请检查设备信息') }
}

async function toggleStatus(device: Device): Promise<void> {
  try {
    const status = device.status === 'ONLINE' ? 'OFFLINE' : 'ONLINE'
    if (isProposal()) await submitApproval({ module: 'device', action: 'UPDATE_STATUS', payload: JSON.stringify({ id: device.id, status }) })
    else { await updateDeviceStatus(device.id, status); await load() }
    showToast('success', isProposal() ? '状态变更已提交，等待管理员审批' : '状态更新成功')
  } catch { showToast('error', '状态更新失败') }
}

async function removeDevice(device: Device): Promise<void> {
  if (!window.confirm(`确定删除设备 ${device.name} 吗？`)) return
  try {
    if (isProposal()) await submitApproval({ module: 'device', action: 'DELETE', payload: JSON.stringify({ id: device.id }) })
    else { await deleteDevice(device.id); await load() }
    showToast('success', isProposal() ? '删除已提交，等待管理员审批' : '设备删除成功')
  } catch { showToast('error', '设备删除失败') }
}

onMounted(load)
</script>

<template>
  <div class="space-y-6">
    <ToastMessage :visible="toast.visible" :type="toast.type" :message="toast.message" @close="toast.visible = false" />
    <div class="flex flex-wrap items-end justify-between gap-4">
      <div><h2 class="text-lg font-bold">设备管理</h2><p class="mt-1 text-sm text-slate-400">管理员直接修改；部门管理者提交审批</p></div>
      <div class="flex flex-wrap items-center gap-3"><div class="flex gap-2 rounded-xl bg-slate-100 p-1"><input v-model="keyword" @keyup.enter="load" placeholder="搜索编码或名称" class="rounded-lg bg-white px-3 py-2 text-sm outline-none" /><button type="button" @click="load" class="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white">查询</button></div><button v-if="props.canManage || props.canPropose" type="button" @click="openCreateDialog" class="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white">新增设备</button></div>
    </div>
    <p v-if="error" class="rounded-lg bg-rose-50 px-4 py-3 text-sm text-rose-600">{{ error }}</p>
    <div class="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm"><table class="w-full text-left text-sm"><thead class="bg-slate-50 text-xs text-slate-400"><tr><th class="px-6 py-4"><button type="button" @click="sortByColumn('code')">编码 {{ sortIndicator('code') }}</button></th><th class="px-6 py-4"><button type="button" @click="sortByColumn('name')">名称 {{ sortIndicator('name') }}</button></th><th class="px-6 py-4"><button type="button" @click="sortByColumn('status')">状态 {{ sortIndicator('status') }}</button></th><th class="px-6 py-4 text-right">操作</th></tr></thead><tbody class="divide-y divide-slate-100"><tr v-if="loading"><td colspan="4" class="px-6 py-10 text-center text-slate-400">加载中...</td></tr><tr v-else-if="sortedDevices.length === 0"><td colspan="4" class="px-6 py-10 text-center text-slate-400">暂无设备数据</td></tr><tr v-for="device in sortedDevices" :key="device.id"><td class="px-6 py-4 font-medium">{{ device.code }}</td><td class="px-6 py-4">{{ device.name }}</td><td class="px-6 py-4">{{ device.status }}</td><td class="space-x-3 px-6 py-4 text-right"><template v-if="props.canManage || props.canPropose"><button type="button" @click="openEditDialog(device)" class="text-indigo-600">编辑</button><button type="button" @click="toggleStatus(device)" class="text-slate-600">切换状态</button><button type="button" @click="removeDevice(device)" class="text-rose-500">删除</button></template><span v-else class="text-xs text-slate-400">只读</span></td></tr></tbody></table></div>
    <ModalDialog :open="dialogOpen" :title="editingDevice ? '编辑设备' : '新增设备'" @close="closeDialog"><form class="space-y-5" @submit.prevent="saveDevice"><input v-model="form.code" required maxlength="64" placeholder="设备编码" class="w-full rounded-lg border px-3 py-2 text-sm" /><input v-model="form.name" required maxlength="128" placeholder="设备名称" class="w-full rounded-lg border px-3 py-2 text-sm" /><div class="flex justify-end gap-3"><button type="button" @click="closeDialog" class="rounded-lg border px-4 py-2 text-sm">取消</button><button type="submit" class="rounded-lg bg-emerald-600 px-5 py-2 text-sm text-white">保存</button></div></form></ModalDialog>
  </div>
</template>
