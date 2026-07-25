package com.example.mes.approval.application;

import com.example.mes.device.api.DeviceController.DeviceRequest;
import com.example.mes.device.application.DeviceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.stereotype.Component;

/** 设备模块审批动作执行器，封装审批数据到设备服务的转换。 */
@Component
public class DeviceApprovalActionExecutor implements ApprovalActionExecutor {
    private final DeviceService deviceService;
    private final ObjectMapper objectMapper;

    /**
     * 创建设备审批执行器。
     *
     * @param deviceService 设备应用服务
     * @param objectMapper JSON 解析器
     */
    public DeviceApprovalActionExecutor(DeviceService deviceService, ObjectMapper objectMapper) {
        this.deviceService = deviceService;
        this.objectMapper = objectMapper;
    }

    /**
     * 判断是否为设备模块动作。
     *
     * @param module 业务模块名称
     * @param action 业务动作名称
     * @return 模块为 device 且动作受支持时返回 true
     */
    @Override
    public boolean supports(String module, String action) {
        return "device".equals(module)
                && ("CREATE".equals(action)
                || "UPDATE".equals(action)
                || "UPDATE_STATUS".equals(action)
                || "DELETE".equals(action));
    }

    /**
     * 执行设备审批变更。
     *
     * @param action 设备动作名称
     * @param payload 设备审批 JSON 载荷
     * @return 无返回值
     * @throws IllegalArgumentException JSON 格式或设备动作参数不合法
     */
    @Override
    public void execute(String action, String payload) {
        try {
            JsonNode data = objectMapper.readTree(payload);
            executeAction(action, data);
        } catch (IOException exception) {
            throw new IllegalArgumentException("审批数据格式错误", exception);
        }
    }

    /**
     * 根据载荷中的动作调用设备服务。
     *
     * @param action 设备动作名称
     * @param data 设备审批数据
     * @return 无返回值
     */
    private void executeAction(String action, JsonNode data) {
        switch (action) {
            case "CREATE" -> deviceService.create(new DeviceRequest(
                    data.path("code").asText(),
                    data.path("name").asText()));
            case "UPDATE" -> deviceService.update(
                    data.path("id").asLong(),
                    new DeviceRequest(data.path("code").asText(), data.path("name").asText()));
            case "UPDATE_STATUS" -> deviceService.changeStatus(
                    data.path("id").asLong(),
                    data.path("status").asText());
            case "DELETE" -> deviceService.delete(data.path("id").asLong());
            default -> throw new IllegalArgumentException("不支持的设备审批动作: " + action);
        }
    }
}
