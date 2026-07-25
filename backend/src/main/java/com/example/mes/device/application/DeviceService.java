package com.example.mes.device.application;

import com.example.mes.api.PageResponse;
import com.example.mes.common.logging.AuditLog;
import com.example.mes.device.api.DeviceController.DeviceRequest;
import com.example.mes.device.api.DeviceResponse;
import com.example.mes.device.domain.DeviceNotFoundException;
import com.example.mes.device.infrastructure.DeviceRepository;
import com.example.mes.domain.device.Device;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 设备应用服务，负责查询和修改设备领域数据。 */
@Service
public class DeviceService {
    private final DeviceRepository repository;

    /**
     * 创建设备应用服务。
     *
     * @param repository 设备持久化仓库
     */
    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    /**
     * 分页查询设备。
     *
     * @param keyword 编码或名称关键词
     * @param status 状态筛选条件
     * @param page 页码，从 0 开始
     * @param size 每页数量
     * @param sortBy 排序字段
     * @param direction 排序方向
     * @return 设备分页响应
     */
    @Transactional(readOnly = true)
    public PageResponse<DeviceResponse> page(
            String keyword,
            String status,
            int page,
            int size,
            String sortBy,
            String direction) {
        String property = switch (sortBy == null ? "id" : sortBy.toLowerCase()) {
            case "code" -> "code";
            case "name" -> "name";
            case "status" -> "status";
            default -> "id";
        };
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, property));
        Page<Device> result = repository.search(keyword == null ? "" : keyword.trim(), status, pageable);
        List<DeviceResponse> responses = result.map(DeviceResponse::from).getContent();
        return PageResponse.of(
                responses,
                page,
                size,
                result.getTotalElements(),
                result.getTotalPages());
    }

    /**
     * 查询单个设备。
     *
     * @param id 设备主键
     * @return 设备响应
     * @throws DeviceNotFoundException 设备不存在
     */
    @Transactional(readOnly = true)
    public DeviceResponse get(Long id) {
        return DeviceResponse.from(find(id));
    }

    /**
     * 查询首页展示的最新设备状态。
     *
     * @return 最新设备状态列表
     */
    @Transactional(readOnly = true)
    public List<DeviceResponse> latestStatuses() {
        return repository.findTop5ByOrderByIdDesc()
                .stream()
                .map(DeviceResponse::from)
                .toList();
    }

    /**
     * 统计在线设备数量。
     *
     * @return 在线设备数量
     */
    @Transactional(readOnly = true)
    public long countOnline() {
        return repository.countByStatus("ONLINE");
    }

    /**
     * 创建设备。
     *
     * @param request 设备编码和名称
     * @return 创建后的设备
     * @throws IllegalArgumentException 设备编码已存在
     */
    @AuditLog(module = "device", action = "CREATE")
    @Transactional
    public DeviceResponse create(DeviceRequest request) {
        if (repository.existsByCode(request.code())) {
            throw new IllegalArgumentException("设备编码已存在: " + request.code());
        }
        Device device = new Device(request.code(), request.name());
        return DeviceResponse.from(repository.save(device));
    }

    /**
     * 更新设备基础信息。
     *
     * @param id 设备主键
     * @param request 新设备编码和名称
     * @return 更新后的设备
     * @throws DeviceNotFoundException 设备不存在
     * @throws IllegalArgumentException 新编码已被其他设备使用
     */
    @AuditLog(module = "device", action = "UPDATE")
    @Transactional
    public DeviceResponse update(Long id, DeviceRequest request) {
        Device device = find(id);
        if (repository.existsByCodeAndIdNot(request.code(), id)) {
            throw new IllegalArgumentException("设备编码已存在: " + request.code());
        }
        device.update(request.code(), request.name());
        return DeviceResponse.from(device);
    }

    /**
     * 修改设备状态。
     *
     * @param id 设备主键
     * @param status 新状态
     * @return 更新后的设备
     * @throws DeviceNotFoundException 设备不存在
     */
    @AuditLog(module = "device", action = "STATUS_CHANGE")
    @Transactional
    public DeviceResponse changeStatus(Long id, String status) {
        Device device = find(id);
        device.changeStatus(status.toUpperCase());
        return DeviceResponse.from(device);
    }

    /**
     * 删除设备。
     *
     * @param id 设备主键
     * @return 无返回值
     * @throws DeviceNotFoundException 设备不存在
     */
    @AuditLog(module = "device", action = "DELETE", level = "WARN")
    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    /**
     * 查询设备领域实体。
     *
     * @param id 设备主键
     * @return 设备领域实体
     * @throws DeviceNotFoundException 设备不存在
     */
    private Device find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }
}
