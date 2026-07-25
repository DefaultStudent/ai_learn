package com.example.mes.device.api;

import com.example.mes.common.api.ApiResponse;
import com.example.mes.api.PageResponse;
import com.example.mes.device.application.DeviceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** 设备管理 REST API：只负责 HTTP 参数校验和调用应用服务。 */
@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceService service;

    /**
     * @param service 设备应用服务
     */
    public DeviceController(DeviceService service) {
        this.service = service;
    }

    /**
     * 分页查询设备，支持设备编码/名称模糊搜索和状态过滤。
     * @param keyword 设备编码或名称关键词
     * @param status 设备状态，可为空
     * @param page 页码，从 0 开始
     * @param size 每页最大 100 条
     * @return 设备分页响应
     */
    @GetMapping
    public ApiResponse<PageResponse<DeviceResponse>> page(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.ok(service.page(keyword, status, page, size));
    }

    /**
     * 查询单台设备详情。
     * @param id 设备主键
     * @return 设备详情
     */
    @GetMapping("/{id}")
    public ApiResponse<DeviceResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(service.get(id));
    }

    /**
     * 创建设备主数据。
     * @param request 设备编码和名称请求体
     * @return 新创建的设备
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DeviceResponse> create(@Valid @RequestBody DeviceRequest request) {
        return ApiResponse.ok(service.create(request));
    }

    /**
     * 更新设备编码和名称。
     * @param id 设备主键
     * @param request 新的设备编码和名称
     * @return 更新后的设备
     */
    @PutMapping("/{id}")
    public ApiResponse<DeviceResponse> update(@PathVariable Long id, @Valid @RequestBody DeviceRequest request) {
        return ApiResponse.ok(service.update(id, request));
    }

    /**
     * 更新设备运行状态。
     * @param id 设备主键
     * @param request 新状态请求体
     * @return 更新后的设备
     */
    @PutMapping("/{id}/status")
    public ApiResponse<DeviceResponse> changeStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return ApiResponse.ok(service.changeStatus(id, request.status()));
    }

    /**
     * 删除设备；真实生产系统通常会改为停用而非物理删除。
     * @param id 设备主键
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /** 设备创建和更新请求。
     * @param code 设备唯一编码
     * @param name 设备显示名称
     */
    public record DeviceRequest(
            @NotBlank @Size(max = 64) String code,
            @NotBlank @Size(max = 128) String name) { }

    /** 设备状态更新请求。
     * @param status 设备运行状态
     */
    public record StatusRequest(@NotBlank @Size(max = 32) String status) { }
}
