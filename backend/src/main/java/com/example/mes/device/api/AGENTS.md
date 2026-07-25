# device API 层

设备 Controller 的路径参数、查询参数和请求体字段必须逐项写 `@param`；请求/响应 DTO 的构造参数也必须有说明。

设备 REST API 统一前缀为 `/api/devices`，返回 `ApiResponse`。请求 DTO 负责输入校验，响应 DTO 不直接暴露 JPA 实体。
