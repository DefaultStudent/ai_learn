<script setup lang="ts">
import ApprovalPanel from '@/components/ApprovalPanel.vue'

defineProps<{ reviewable: boolean }>()
</script>

<template>
  <ApprovalPanel :reviewable="reviewable" />
</template>
