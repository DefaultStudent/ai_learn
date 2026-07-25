package com.example.mes.device.domain;

import com.example.mes.common.exception.ResourceNotFoundException;

/** 设备不存在时的业务异常。 */
public class DeviceNotFoundException extends ResourceNotFoundException {
    /**
     * @param id 不存在的设备主键
     */
    public DeviceNotFoundException(Long id) {
        super("设备不存在: " + id);
    }
}
