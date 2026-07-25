import { ref } from 'vue'
import { getDashboardSummary, type DashboardSummary } from '@/api/dashboard'

/** 管理生产总览数据加载状态。 */
export function useDashboard() {
  const loading = ref(false)
  const summary = ref<DashboardSummary>({ activeOrders: 0, onlineDevices: 0, todayOutput: 0, qualityRate: 0, devices: [] })

  /**
   * 加载生产总览数据。
   *
   * @return 无返回值
   */
  async function loadSummary(): Promise<void> {
    loading.value = true
    try {
      summary.value = await getDashboardSummary()
    } finally {
      loading.value = false
    }
  }

  return { loading, summary, loadSummary }
}
