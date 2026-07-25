<script setup lang="ts">
import { onMounted } from 'vue'
import DashboardOverview from '@/components/DashboardOverview.vue'
import { useDashboard } from '@/composables/useDashboard'

const { loading, summary, loadSummary } = useDashboard()
onMounted(() => void loadSummary())
</script>

<template>
  <DashboardOverview :summary="summary" :loading="loading" />
</template>
