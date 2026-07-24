# API 层

Controller 只负责 HTTP 参数校验、调用应用服务、映射响应。URL 使用复数资源名，统一以 `/api` 开头；响应优先包装为 `ApiResponse`，错误由全局异常处理器统一转换。
