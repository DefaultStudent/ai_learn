# API 层

Controller、异常处理器和 DTO 的每个构造器/方法都必须使用 Javadoc；参数列表中的每个参数必须有对应的 `@param`，响应方法必须有 `@return`，异常转换方法必须说明 HTTP 状态含义。

这是后端全局 API 层，负责跨业务模块复用的分页响应、异常处理和 HTTP 契约。Controller 只负责 HTTP 参数校验、调用应用服务、映射响应；URL 使用复数资源名，统一以 `/api` 开头；成功响应包装为 `ApiResponse`，分页响应使用 `PageResponse`，错误由 `GlobalExceptionHandler` 统一转换。

业务模块自己的 `api/` 目录只放该模块的 Controller、请求 DTO 和响应 DTO，不要重复实现全局异常处理或分页结构。
