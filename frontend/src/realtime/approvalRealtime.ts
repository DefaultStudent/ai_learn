export interface ApprovalRealtimeEvent { id: number; requester: string; module: string; action: string; status: string }

type ApprovalEventHandler = (event: ApprovalRealtimeEvent) => void
type CentrifugoMessage = {
  connect?: { subs?: Record<string, unknown> }
  error?: { code?: number; message?: string }
  push?: { pub?: { data?: ApprovalRealtimeEvent } }
  ping?: Record<string, never>
}

export function connectApprovalRealtime(token: string, onEvent: ApprovalEventHandler): () => void {
  let socket: WebSocket | null = null
  let closed = false
  let retryTimer: number | undefined
  const endpoint = window.location.port === '5173'
    ? `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.hostname}:8000/connection/websocket`
    : `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}/connection/websocket`
  function connect(): void {
    if (closed) return
    socket = new WebSocket(endpoint)
    socket.onopen = () => socket?.send(JSON.stringify({ id: 1, connect: { token } }))
    socket.onmessage = (message) => {
      if (typeof message.data !== 'string') return
      let response: CentrifugoMessage
      try { response = JSON.parse(message.data) as CentrifugoMessage } catch { return }
      if (response.ping !== undefined) {
        socket?.send(JSON.stringify({ pong: {} }))
        return
      }
      if (response.error) { socket?.close(); return }
      const event = response.push?.pub?.data
      if (event) onEvent(event)
    }
    socket.onclose = () => { if (!closed) retryTimer = window.setTimeout(connect, 3000) }
    socket.onerror = () => socket?.close()
  }
  connect()
  return () => { closed = true; if (retryTimer !== undefined) window.clearTimeout(retryTimer); socket?.close(); socket = null }
}
