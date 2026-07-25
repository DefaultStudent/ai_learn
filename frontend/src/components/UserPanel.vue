<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { createUser, deleteUser, listUsers, updateUser, type ManagedUser, type UserForm } from '@/api/users'
import type { UserRole } from '@/api/auth'
import ModalDialog from './ModalDialog.vue'
import ToastMessage from './ToastMessage.vue'

const users = ref<ManagedUser[]>([])
const dialogOpen = ref(false)
const editing = ref<ManagedUser | null>(null)
const error = ref('')
const toast = ref({ visible: false, type: 'success' as 'success' | 'error', message: '' })
const form = ref<UserForm>({ username: '', password: '', displayName: '', role: 'DEVICE_USER', department: '' })
type UserSortField = 'username' | 'displayName' | 'role' | 'department'
const sortBy = ref<UserSortField>('username')
const direction = ref<'asc' | 'desc'>('asc')
const roles: Array<{ value: UserRole; label: string }> = [
  { value: 'SYSTEM_ADMIN', label: '系统管理员' }, { value: 'ADMIN', label: '管理员' },
  { value: 'PRODUCTION_MANAGER', label: '生产部门管理者' }, { value: 'DEVICE_MANAGER', label: '设备部门管理者' },
  { value: 'PRODUCTION_USER', label: '生产部门普通用户' }, { value: 'DEVICE_USER', label: '设备部门普通用户' },
]
const roleLabel = (role: UserRole) => roles.find((item) => item.value === role)?.label ?? role
const sortedUsers = computed(() => [...users.value].sort((left, right) => {
  const comparison = String(left[sortBy.value] ?? '').localeCompare(String(right[sortBy.value] ?? ''), 'zh-CN', { numeric: true })
  return direction.value === 'asc' ? comparison : -comparison
}))
function sortByColumn(field: UserSortField) { if (sortBy.value === field) direction.value = direction.value === 'asc' ? 'desc' : 'asc'; else { sortBy.value = field; direction.value = 'asc' } }
function sortIndicator(field: UserSortField) { return sortBy.value === field ? (direction.value === 'asc' ? '↑' : '↓') : '↕' }
async function load() { try { users.value = await listUsers() } catch { error.value = '用户数据加载失败' } }
function openCreate() { editing.value = null; form.value = { username: '', password: '', displayName: '', role: 'DEVICE_USER', department: '' }; dialogOpen.value = true }
function openEdit(user: ManagedUser) { editing.value = user; form.value = { username: user.username, password: '', displayName: user.displayName, role: user.role, department: user.department ?? '' }; dialogOpen.value = true }
function closeDialog() { dialogOpen.value = false; editing.value = null }
async function save() { const isEditing = Boolean(editing.value); try { if (editing.value) await updateUser(editing.value.id, form.value); else await createUser(form.value); closeDialog(); await load(); toast.value = { visible: true, type: 'success', message: isEditing ? '用户修改成功' : '用户创建成功' } } catch { toast.value = { visible: true, type: 'error', message: isEditing ? '用户修改失败，请检查账号或密码' : '用户创建失败，请检查账号或密码' } } }
async function remove(user: ManagedUser) { if (!window.confirm(`确定删除用户 ${user.displayName} 吗？`)) return; try { await deleteUser(user.id); await load(); toast.value = { visible: true, type: 'success', message: '用户删除成功' } } catch { toast.value = { visible: true, type: 'error', message: '用户删除失败' } } }
onMounted(load)
</script>

<template>
  <div class="space-y-6"><ToastMessage :visible="toast.visible" :type="toast.type" :message="toast.message" @close="toast.visible = false" /><div class="flex items-end justify-between"><div><h2 class="text-lg font-bold">用户管理</h2><p class="mt-1 text-sm text-slate-400">系统管理员可维护账号、角色和部门权限</p></div><button @click="openCreate" class="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white">新增用户</button></div><p v-if="error" class="rounded-lg bg-rose-50 px-4 py-3 text-sm text-rose-600">{{ error }}</p><div class="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm"><table class="w-full text-left text-sm"><thead class="bg-slate-50 text-xs text-slate-400"><tr><th class="px-6 py-4"><button @click="sortByColumn('username')">用户名 {{ sortIndicator('username') }}</button></th><th class="px-6 py-4"><button @click="sortByColumn('displayName')">显示名称 {{ sortIndicator('displayName') }}</button></th><th class="px-6 py-4"><button @click="sortByColumn('role')">角色 {{ sortIndicator('role') }}</button></th><th class="px-6 py-4"><button @click="sortByColumn('department')">部门 {{ sortIndicator('department') }}</button></th><th class="px-6 py-4 text-right">操作</th></tr></thead><tbody class="divide-y divide-slate-100"><tr v-for="user in sortedUsers" :key="user.id"><td class="px-6 py-4 font-medium">{{ user.username }}</td><td class="px-6 py-4">{{ user.displayName }}</td><td class="px-6 py-4">{{ roleLabel(user.role) }}</td><td class="px-6 py-4">{{ user.department || '-' }}</td><td class="space-x-3 px-6 py-4 text-right"><button @click="openEdit(user)" class="text-indigo-600">编辑</button><button @click="remove(user)" class="text-rose-500">删除</button></td></tr></tbody></table></div><ModalDialog :open="dialogOpen" :title="editing ? '编辑用户' : '新增用户'" @close="closeDialog"><form class="space-y-4" @submit.prevent="save"><input v-model="form.username" required placeholder="用户名" class="w-full rounded-lg border px-3 py-2 text-sm" /><input v-model="form.password" :required="!editing" type="password" minlength="6" placeholder="密码（编辑时留空表示不修改）" class="w-full rounded-lg border px-3 py-2 text-sm" /><input v-model="form.displayName" required placeholder="显示名称" class="w-full rounded-lg border px-3 py-2 text-sm" /><select v-model="form.role" class="w-full rounded-lg border px-3 py-2 text-sm"><option v-for="role in roles" :key="role.value" :value="role.value">{{ role.label }}</option></select><input v-model="form.department" placeholder="所属部门" class="w-full rounded-lg border px-3 py-2 text-sm" /><div class="flex justify-end gap-3"><button type="button" @click="closeDialog" class="rounded-lg border px-4 py-2 text-sm">取消</button><button type="submit" class="rounded-lg bg-indigo-600 px-4 py-2 text-sm text-white">保存</button></div></form></ModalDialog></div>
</template>
