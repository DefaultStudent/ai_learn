package com.example.mes.dashboard.api;

import com.example.mes.common.api.ApiResponse;
import com.example.mes.device.api.DeviceResponse;
import com.example.mes.device.application.DeviceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DeviceService deviceService;

    /**
     * @param deviceService 设备应用服务，用于读取主页状态数据
     */
    public DashboardController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 学习用生产总览接口；后续应由应用服务聚合订单、设备和质量数据。
     * @return 主页指标和最近设备状态
     */
    @GetMapping("/summary")
    public ApiResponse<Summary> summary() {
        List<DeviceResponse> devices = deviceService.latestStatuses();
        return ApiResponse.ok(new Summary(12, (int) deviceService.countOnline(), 1840, 98.6, devices));
    }

    /** 前台仪表盘所需的最小聚合指标。
     * @param activeOrders 进行中的工单数量
     * @param onlineDevices 在线设备数量
     * @param todayOutput 今日产量
     * @param qualityRate 一次合格率百分比
     * @param devices 主页展示的最近设备状态
     */
    public record Summary(
            int activeOrders,
            int onlineDevices,
            int todayOutput,
            double qualityRate,
            List<DeviceResponse> devices) {
        public Summary { }
    }
}
