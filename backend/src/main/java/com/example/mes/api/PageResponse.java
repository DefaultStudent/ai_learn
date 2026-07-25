package com.example.mes.api;

import java.util.List;

/** 所有分页 REST API 共用的响应结构。
 * @param items 当前页数据
 * @param page 当前页码
 * @param size 每页数量
 * @param total 总记录数
 * @param totalPages 总页数
 * @param <T> 列表元素类型
 */
public record PageResponse<T>(List<T> items, int page, int size, long total, int totalPages) {
    /** 创建分页响应。
     * @param items 当前页数据
     * @param page 当前页码，从 0 开始
     * @param size 每页数量
     * @param total 总记录数
     * @param totalPages 总页数
     * @param <T> 列表元素类型
     * @return 分页响应对象
     */
    public static <T> PageResponse<T> of(
            List<T> items,
            int page,
            int size,
            long total,
            int totalPages) {
        return new PageResponse<>(items, page, size, total, totalPages);
    }
}
