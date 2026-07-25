<script setup lang="ts">
defineProps<{ open: boolean; title: string }>()
const emit = defineEmits<{ close: [] }>()
</script>

<template>
  <UModal :open="open" :title="title" @update:open="(value: boolean) => !value && emit('close')">
    <template #body><slot /></template>
  </UModal>
</template>
