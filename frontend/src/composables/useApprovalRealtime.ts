import { onBeforeUnmount, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listApprovals, type ApprovalRequest } from '@/api/approvals'
import { getApprovalRealtimeToken } from '@/api/realtime'
import { connectApprovalRealtime, type ApprovalRealtimeEvent } from '@/realtime/approvalRealtime'
import { useAuthStore } from '@/stores/auth'

/** 管理审批待办查询、实时事件和审批提醒弹窗。 */
export function useApprovalRealtime() {
  const auth = useAuthStore()
  const router = useRouter()
  const pendingApprovals = ref<ApprovalRequest[]>([])
  const approvalNoticeOpen = ref(false)
  const knownPendingApprovalIds = ref<Set<number>>(new Set())
  let closeConnection: (() => void) | undefined

  /**
   * 加载管理员待处理审批。
   *
   * @param showExisting 是否显示已经存在的待处理申请
   * @return 无返回值
   */
  async function loadPendingApprovals(showExisting: boolean): Promise<void> {
    if (!auth.canReviewApprovals) return
    const currentPending = (await listApprovals()).filter((request) => request.status === 'PENDING')
    const newRequestArrived = currentPending.some((request) => !knownPendingApprovalIds.value.has(request.id))
    pendingApprovals.value = currentPending
    knownPendingApprovalIds.value = new Set(currentPending.map((request) => request.id))
    if (showExisting || newRequestArrived) approvalNoticeOpen.value = currentPending.length > 0
  }

  /**
   * 处理实时审批事件，并通知审批列表刷新。
   *
   * @param event 审批状态事件
   * @return 无返回值
   */
  function handleApprovalEvent(event: ApprovalRealtimeEvent): void {
    window.dispatchEvent(new CustomEvent<ApprovalRealtimeEvent>('approval-updated', { detail: event }))
    if (!auth.canReviewApprovals || event.status !== 'PENDING') return
    if (pendingApprovals.value.some((request) => request.id === event.id)) return
    pendingApprovals.value = [...pendingApprovals.value, {
      id: event.id,
      requester: event.requester,
      module: event.module,
      action: event.action,
      payload: '',
      status: 'PENDING',
      reviewer: null,
      reviewRemark: null,
      createdAt: new Date().toISOString(),
      reviewedAt: null,
    }]
    knownPendingApprovalIds.value.add(event.id)
    approvalNoticeOpen.value = true
  }

  /**
   * 连接审批实时频道。
   *
   * @return 无返回值
   */
  async function start(): Promise<void> {
    if (!auth.user) return
    try {
      const token = await getApprovalRealtimeToken()
      if (auth.user) closeConnection = connectApprovalRealtime(token, handleApprovalEvent)
    } catch {
      closeConnection = undefined
    }
  }

  /**
   * 打开审批页面并关闭提醒弹窗。
   *
   * @return 无返回值
   */
  async function openApprovalPage(): Promise<void> {
    approvalNoticeOpen.value = false
    await router.push({ name: 'approvals' })
  }

  onBeforeUnmount(() => closeConnection?.())
  return { pendingApprovals, approvalNoticeOpen, loadPendingApprovals, start, openApprovalPage }
}
