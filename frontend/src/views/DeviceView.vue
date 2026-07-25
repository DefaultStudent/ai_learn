<script setup lang="ts">
import DevicePanel from '@/components/DevicePanel.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
</script>

<template>
  <DevicePanel :can-manage="auth.canManageDevices" :can-propose="auth.canProposeDeviceChanges" />
</template>
