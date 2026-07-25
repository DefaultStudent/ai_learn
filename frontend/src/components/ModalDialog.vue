<script setup lang="ts">
defineProps<{ open: boolean; title: string }>()
const emit = defineEmits<{ close: [] }>()

// 点击遮罩层关闭，点击对话框内容不触发关闭。
function closeOnBackdrop(event: MouseEvent) {
  if (event.target === event.currentTarget) emit('close')
}
</script>

<template>
  <Teleport to="body">
    <div v-if="open" class="fixed inset-0 z-50 grid place-items-center bg-slate-900/40 p-4" @click="closeOnBackdrop">
      <section role="dialog" aria-modal="true" :aria-label="title" class="w-full max-w-lg rounded-2xl bg-white shadow-2xl">
        <header class="flex items-center justify-between border-b border-slate-100 px-6 py-4">
          <h2 class="text-lg font-bold text-slate-800">{{ title }}</h2>
          <button type="button" aria-label="关闭对话框" class="rounded-lg px-2 py-1 text-xl text-slate-400 hover:bg-slate-100 hover:text-slate-700" @click="emit('close')">×</button>
        </header>
        <div class="p-6"><slot /></div>
      </section>
    </div>
  </Teleport>
</template>
