<script setup lang="ts">
import { onBeforeUnmount, watch } from 'vue'

type ToastType = 'success' | 'error'
const props = defineProps<{ visible: boolean; type: ToastType; message: string }>()
const emit = defineEmits<{ close: [] }>()
let timer: ReturnType<typeof setTimeout> | undefined

// 每次显示新消息都重新计时，五秒后自动关闭。
watch(() => [props.visible, props.message], ([visible]) => {
  if (timer) clearTimeout(timer)
  if (visible) timer = setTimeout(() => emit('close'), 5000)
})
onBeforeUnmount(() => { if (timer) clearTimeout(timer) })
</script>

<template>
  <Transition enter-active-class="transition duration-200" enter-from-class="translate-x-4 opacity-0" enter-to-class="translate-x-0 opacity-100" leave-active-class="transition duration-200" leave-from-class="opacity-100" leave-to-class="translate-x-4 opacity-0">
    <div v-if="visible" class="fixed right-6 top-6 z-[60] flex w-80 items-start gap-3 rounded-xl border px-4 py-3 shadow-lg" :class="type === 'success' ? 'border-emerald-200 bg-emerald-50 text-emerald-800' : 'border-rose-200 bg-rose-50 text-rose-800'" role="status" aria-live="polite">
      <span class="mt-0.5 text-lg">{{ type === 'success' ? '✓' : '!' }}</span>
      <p class="flex-1 text-sm font-medium">{{ message }}</p>
      <button type="button" aria-label="关闭消息" class="rounded px-1 text-lg leading-none opacity-60 hover:bg-black/5 hover:opacity-100" @click="emit('close')">×</button>
    </div>
  </Transition>
</template>
