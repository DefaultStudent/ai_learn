import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

/** 管理前台登录会话、初始化状态和退出后的路由跳转。 */
export function useSession() {
  const auth = useAuthStore()
  const router = useRouter()
  const sessionReady = ref(false)

  /**
   * 初始化页面会话，确保每次进入页面都从登录状态开始。
   *
   * @return 无返回值
   */
  async function initialize(): Promise<void> {
    await auth.signOut()
    sessionReady.value = true
  }

  /**
   * 清理当前会话并跳转到登录页。
   *
   * @return 无返回值
   */
  async function signOut(): Promise<void> {
    await auth.signOut()
    await router.push({ name: 'login' })
  }

  return { auth, sessionReady, initialize, signOut }
}
