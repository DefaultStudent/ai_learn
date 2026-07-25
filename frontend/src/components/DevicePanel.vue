<script setup lang="ts">
import { computed, h, onMounted, ref } from 'vue'
import UButton from '@nuxt/ui/components/Button.vue'
import type { TableColumn } from '@nuxt/ui'
import { submitApproval } from '@/api/approvals'
import { createDevice, deleteDevice, listDevices, updateDevice, updateDeviceStatus, type Device, type DeviceSortField } from '@/api/devices'
import ModalDialog from './ModalDialog.vue'
import ToastMessage from './ToastMessage.vue'

type ToastType = 'success' | 'error'
const props = defineProps<{ canManage: boolean; canPropose: boolean }>()
const devices = ref<Device[]>([]); const keyword = ref(''); const sortBy = ref<DeviceSortField>('code'); const direction = ref<'asc' | 'desc'>('asc'); const page = ref(1); const pageSize = ref(10); const loading = ref(false); const error = ref(''); const dialogOpen = ref(false); const editingDevice = ref<Device | null>(null); const form = ref({ code: '', name: '' }); const toast = ref({ visible: false, type: 'success' as ToastType, message: '' })
const pageSizeItems = [{ label: '10 条 / 页', value: 10 }, { label: '20 条 / 页', value: 20 }, { label: '50 条 / 页', value: 50 }]
const sortedDevices = computed(() => [...devices.value].sort((a, b) => { const result = sortBy.value === 'id' ? a.id - b.id : String(a[sortBy.value]).localeCompare(String(b[sortBy.value]), 'zh-CN', { numeric: true }); return direction.value === 'asc' ? result : -result }))
const pagedDevices = computed(() => sortedDevices.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value))
const columns: TableColumn<Device>[] = [
  { accessorKey: 'code', header: () => sortHeader('code', '编码') },
  { accessorKey: 'name', header: () => sortHeader('name', '名称') },
  { accessorKey: 'status', header: () => sortHeader('status', '状态') },
  { id: 'actions', header: '操作' },
]
function sortHeader(field: DeviceSortField, label: string): ReturnType<typeof h> { return h(UButton, { variant: 'ghost', color: 'neutral', size: 'xs', label, onClick: () => sortByColumn(field) }) }
function sortByColumn(field: DeviceSortField): void { if (sortBy.value === field) direction.value = direction.value === 'asc' ? 'desc' : 'asc'; else { sortBy.value = field; direction.value = 'asc' }; page.value = 1 }
function showToast(type: ToastType, message: string): void { toast.value = { visible: true, type, message } }
async function load(): Promise<void> { loading.value = true; error.value = ''; try { devices.value = (await listDevices({ keyword: keyword.value, page: 0, size: 100, sortBy: 'code', direction: 'asc' })).items; page.value = 1 } catch { error.value = '设备数据加载失败，请检查后端服务' } finally { loading.value = false } }
function openCreateDialog(): void { editingDevice.value = null; form.value = { code: '', name: '' }; dialogOpen.value = true }
function openEditDialog(device: Device): void { editingDevice.value = device; form.value = { code: device.code, name: device.name }; dialogOpen.value = true }
function closeDialog(): void { dialogOpen.value = false; editingDevice.value = null }
function isProposal(): boolean { return props.canPropose && !props.canManage }
async function saveDevice(): Promise<void> { if (!form.value.code || !form.value.name) { showToast('error', '请填写设备编码和名称'); return }; const editing = editingDevice.value !== null; try { if (isProposal()) await submitApproval({ module: 'device', action: editing ? 'UPDATE' : 'CREATE', payload: JSON.stringify({ id: editingDevice.value?.id, ...form.value }) }); else if (editingDevice.value) await updateDevice(editingDevice.value.id, form.value); else await createDevice(form.value); closeDialog(); if (!isProposal()) await load(); showToast('success', isProposal() ? '变更已提交，等待管理员审批' : editing ? '设备修改成功' : '设备添加成功') } catch { showToast('error', '操作失败，请检查设备信息') } }
async function toggleStatus(device: Device): Promise<void> { try { const status = device.status === 'ONLINE' ? 'OFFLINE' : 'ONLINE'; if (isProposal()) await submitApproval({ module: 'device', action: 'UPDATE_STATUS', payload: JSON.stringify({ id: device.id, status }) }); else { await updateDeviceStatus(device.id, status); await load() }; showToast('success', isProposal() ? '状态变更已提交，等待审批' : '状态更新成功') } catch { showToast('error', '状态更新失败') } }
async function removeDevice(device: Device): Promise<void> { if (!window.confirm(`确定删除设备 ${device.name} 吗？`)) return; try { if (isProposal()) await submitApproval({ module: 'device', action: 'DELETE', payload: JSON.stringify({ id: device.id }) }); else { await deleteDevice(device.id); await load() }; showToast('success', isProposal() ? '删除已提交，等待审批' : '设备删除成功') } catch { showToast('error', '设备删除失败') } }
function changePageSize(value: number): void { pageSize.value = value; page.value = 1 }
onMounted(() => void load())
</script>

<template><div class="space-y-6"><ToastMessage :visible="toast.visible" :type="toast.type" :message="toast.message" @close="toast.visible = false" /><div class="flex flex-wrap items-end justify-between gap-4"><div><h2 class="text-xl font-bold text-highlighted">设备管理</h2><p class="mt-1 text-sm text-muted">管理员直接修改，部门管理者提交审批。</p></div><div class="flex flex-wrap gap-3"><div class="flex gap-2"><UInput v-model="keyword" placeholder="搜索编码或名称" /><UButton variant="outline" color="primary" @click="load">查询</UButton></div><UButton v-if="props.canManage || props.canPropose" color="success" variant="solid" @click="openCreateDialog">新增设备</UButton></div></div><UAlert v-if="error" color="error" variant="soft" :title="error" /><UCard><UTable :data="pagedDevices" :columns="columns" :loading="loading" empty="暂无设备数据"><template #status-cell="{ row }"><UBadge :color="row.original.status === 'ONLINE' ? 'success' : 'neutral'" variant="subtle">{{ row.original.status }}</UBadge></template><template #actions-cell="{ row }"><div v-if="props.canManage || props.canPropose" class="flex justify-end gap-2"><UButton size="xs" variant="soft" @click="openEditDialog(row.original)">编辑</UButton><UButton size="xs" color="neutral" variant="soft" @click="toggleStatus(row.original)">切换状态</UButton><UButton size="xs" color="error" variant="soft" @click="removeDevice(row.original)">删除</UButton></div><span v-else class="text-xs text-muted">只读</span></template></UTable><div class="mt-5 flex flex-wrap items-center justify-between gap-4 border-t border-default pt-4"><div class="flex items-center gap-2 text-sm text-muted"><span>每页显示</span><USelect :model-value="pageSize" :items="pageSizeItems" value-key="value" class="w-32" @update:model-value="changePageSize" /><span>共 {{ sortedDevices.length }} 条</span></div><UPagination v-if="sortedDevices.length > pageSize" v-model:page="page" :total="sortedDevices.length" :items-per-page="pageSize" /></div></UCard><ModalDialog :open="dialogOpen" :title="editingDevice ? '编辑设备' : '新增设备'" @close="closeDialog"><form class="space-y-5" @submit.prevent="saveDevice"><UFormField label="设备编码" required><UInput v-model="form.code" maxlength="64" class="w-full" /></UFormField><UFormField label="设备名称" required><UInput v-model="form.name" maxlength="128" class="w-full" /></UFormField><div class="flex justify-end gap-3"><UButton type="button" variant="outline" color="neutral" @click="closeDialog">取消</UButton><UButton type="submit" color="success" variant="solid">保存</UButton></div></form></ModalDialog></div></template>
