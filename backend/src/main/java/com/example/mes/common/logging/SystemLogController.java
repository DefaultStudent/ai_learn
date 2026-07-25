package com.example.mes.common.logging;

import com.example.mes.api.PageResponse;
import com.example.mes.common.api.ApiResponse;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 系统日志查询 API，仅系统管理员可访问。 */
@RestController
@RequestMapping("/api/logs")
@PreAuthorize("hasAuthority('PERM_SYSTEM_LOG_VIEW')")
public class SystemLogController {
    private final SystemLogRepository repository;
    /** @param repository 系统日志仓储 */
    public SystemLogController(SystemLogRepository repository) {
        this.repository = repository;
    }
    /**
     * 分页查询系统日志。
     * @param page 页码，从 0 开始
     * @param size 每页数量
     * @return 日志分页响应
     */
    @GetMapping
    public ApiResponse<PageResponse<SystemLog>> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // 限制单页最大数量，避免日志查询一次性占用过多内存。
        org.springframework.data.domain.Page<SystemLog> result = repository.findAll(
                PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createdAt")));
        List<SystemLog> items = result.getContent();
        return ApiResponse.ok(PageResponse.of(items, page, size, result.getTotalElements(), result.getTotalPages()));
    }
}
