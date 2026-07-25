package com.example.mes.common.api;

/** REST API 的统一响应包装，便于前台按固定结构解析。
 * @param data 响应数据
 * @param message 响应消息
 * @param <T> 数据类型
 */
public record ApiResponse<T>(T data, String message) {
    /** 创建一条默认消息为 OK 的成功响应。 */
    /**
     * @param data 成功响应的数据
     * @param <T> 数据类型
     * @return 统一成功响应
     */
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(data, "OK"); }
}
