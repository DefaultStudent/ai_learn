package com.example.mes.device.application;

import com.example.mes.device.api.DeviceController.DeviceRequest;
import com.example.mes.device.api.DeviceResponse;
import com.example.mes.api.PageResponse;
import com.example.mes.common.logging.SystemLogService;
import com.example.mes.device.domain.DeviceNotFoundException;
import com.example.mes.device.infrastructure.DeviceRepository;
import com.example.mes.domain.device.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 设备应用服务：编排查询、事务和领域对象变更。 */
@Service
public class DeviceService {
    private final DeviceRepository repository;
    private final SystemLogService systemLogService;

    /**
     * @param repository 设备持久化仓储
     * @param systemLogService 系统日志服务
     */
    public DeviceService(DeviceRepository repository, SystemLogService systemLogService) {
        this.repository = repository;
        this.systemLogService = systemLogService;
    }

    @Transactional(readOnly = true)
    /**
     * 分页查询设备。
     * @param keyword 设备编码或名称关键词
     * @param status 设备状态过滤条件，可为空
     * @param page 页码，从 0 开始
     * @param size 每页数量
     * @return 设备分页响应
     */
    public PageResponse<DeviceResponse> page(String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Device> result = repository.search(keyword == null ? "" : keyword.trim(), status, pageable);
        return PageResponse.of(result.map(DeviceResponse::from).getContent(), page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    /**
     * 查询单台设备。
     * @param id 设备主键
     * @return 设备响应 DTO
     * @throws DeviceNotFoundException 设备不存在时抛出
     */
    public DeviceResponse get(Long id) {
        return DeviceResponse.from(find(id));
    }

    /**
     * 提供主页设备状态卡片使用的轻量数据集。
     * @return 按设备 ID 倒序排列的最近设备
     */
    @Transactional(readOnly = true)
    public List<DeviceResponse> latestStatuses() {
        return repository.findTop5ByOrderByIdDesc().stream().map(DeviceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    /**
     * 统计全部设备中的在线数量。
     * @return 在线设备数量
     */
    public long countOnline() { return repository.countByStatus("ONLINE"); }

    @Transactional
    /**
     * 创建设备。
     * @param request 设备编码和名称
     * @return 保存后的设备
     * @throws IllegalArgumentException 编码已存在时抛出
     */
    public DeviceResponse create(DeviceRequest request) {
        if (repository.existsByCode(request.code()))
            throw new IllegalArgumentException("设备编码已存在: " + request.code());
        Device saved = repository.save(new Device(request.code(), request.name()));
        systemLogService.info("device", "CREATE", "创建设备: " + saved.getCode());
        return DeviceResponse.from(saved);
    }

    @Transactional
    /**
     * 更新设备主数据。
     * @param id 设备主键
     * @param request 新的设备编码和名称
     * @return 更新后的设备
     * @throws DeviceNotFoundException 设备不存在时抛出
     * @throws IllegalArgumentException 新编码已存在时抛出
     */
    public DeviceResponse update(Long id, DeviceRequest request) {
        Device device = find(id);
        if (repository.existsByCodeAndIdNot(request.code(), id))
            throw new IllegalArgumentException("设备编码已存在: " + request.code());
        device.update(request.code(), request.name());
        systemLogService.info("device", "UPDATE", "更新设备: " + device.getCode());
        return DeviceResponse.from(device);
    }

    @Transactional
    /**
     * 更新设备运行状态。
     * @param id 设备主键
     * @param status 新状态值
     * @return 更新后的设备
     * @throws DeviceNotFoundException 设备不存在时抛出
     */
    public DeviceResponse changeStatus(Long id, String status) {
        Device device = find(id);
        device.changeStatus(status.toUpperCase());
        systemLogService.info("device", "STATUS_CHANGE", "设备 " + device.getCode() + " 状态变更为 " + device.getStatus());
        return DeviceResponse.from(device);
    }

    @Transactional
    /**
     * 删除设备。
     * @param id 设备主键
     * @throws DeviceNotFoundException 设备不存在时抛出
     */
    public void delete(Long id) {
        Device device = find(id);
        repository.delete(device);
        systemLogService.warn("device", "DELETE", "删除设备: " + device.getCode());
    }

    /**
     * 查找设备领域实体。
     * @param id 设备主键
     * @return 设备领域实体
     * @throws DeviceNotFoundException 设备不存在时抛出
     */
    private Device find(Long id) {
        return repository.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
    }
}
