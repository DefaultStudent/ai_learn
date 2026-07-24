package com.example.mes.common.api;

/** REST API 的统一成功响应包装，便于前台按固定结构解析。 */
public record ApiResponse<T>(T data, String message) {
    /** 创建一条默认消息为 OK 的成功响应。 */
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(data, "OK"); }
}
