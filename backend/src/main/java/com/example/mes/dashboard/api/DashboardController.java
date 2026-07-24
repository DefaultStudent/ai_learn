package com.example.mes.dashboard.api;

import com.example.mes.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    /** 学习用生产总览接口；后续应由应用服务聚合订单、设备和质量数据。 */
    @GetMapping("/summary")
    public ApiResponse<Summary> summary() { return ApiResponse.ok(new Summary(12, 28, 1840, 98.6)); }

    /** 前台仪表盘所需的最小聚合指标。 */
    public record Summary(int activeOrders, int onlineDevices, int todayOutput, double qualityRate) {}
}
