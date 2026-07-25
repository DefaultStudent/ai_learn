<script setup lang="ts">
import { onBeforeUnmount, watch } from 'vue'
type ToastType = 'success' | 'error'
const props = defineProps<{ visible: boolean; type: ToastType; message: string }>()
const emit = defineEmits<{ close: [] }>()
let timer: ReturnType<typeof setTimeout> | undefined
watch(() => [props.visible, props.message], ([visible]) => { if (timer) clearTimeout(timer); if (visible) timer = setTimeout(() => emit('close'), 5000) })
onBeforeUnmount(() => { if (timer) clearTimeout(timer) })
</script>

<template><UAlert v-if="visible" class="fixed right-6 top-6 z-50 w-80 shadow-lg" :color="type === 'success' ? 'success' : 'error'" variant="solid" :title="message" close @update:open="emit('close')" /></template>
