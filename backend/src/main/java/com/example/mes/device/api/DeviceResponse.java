package com.example.mes.device.api;

import com.example.mes.domain.device.Device;

/** 对外返回 DTO，避免直接暴露 JPA 实体。
 * @param id 设备主键
 * @param code 设备编码
 * @param name 设备名称
 * @param status 设备状态
 */
public record DeviceResponse(Long id, String code, String name, String status) {
    /**
     * 将领域实体转换为 API 响应 DTO。
     * @param device 设备领域实体
     * @return 对外设备响应
     */
    public static DeviceResponse from(Device device) {
        return new DeviceResponse(device.getId(), device.getCode(), device.getName(), device.getStatus());
    }
}
