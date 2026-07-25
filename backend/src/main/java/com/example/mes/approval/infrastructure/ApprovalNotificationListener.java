package com.example.mes.approval.infrastructure;

import com.example.mes.approval.application.ApprovalEvent;
import java.util.Map;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** 消费 RabbitMQ 审批事件并发布到 Centrifugo。 */
@Component
public class ApprovalNotificationListener {
    private final RestClient client;
    private final String apiKey;

    /** @param builder HTTP 客户端构建器 @param baseUrl Centrifugo 地址 @param apiKey Centrifugo API 密钥 */
    public ApprovalNotificationListener(
            RestClient.Builder builder,
            @Value("${mes.realtime.centrifugo.url:http://localhost:8000}") String baseUrl,
            @Value("${mes.realtime.centrifugo.api-key:mes-realtime-api-key}") String apiKey) {
        this.client = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    /** @param event RabbitMQ 审批事件 */
    @RabbitListener(queues = "mes.approval.notifications")
    public void publishToCentrifugo(ApprovalEvent event) {
        publish("approval:admins", event);
        publish("approval:requester:" + event.requester(), event);
    }

    /**
     * @param channel Centrifugo 频道名称
     * @param event 待发布的审批事件
     */
    private void publish(String channel, ApprovalEvent event) {
        // 管理员和申请人使用不同频道，避免申请人收到其他人的审批事件。
        client.post().uri("/api/publish").header("X-API-Key", apiKey).contentType(MediaType.APPLICATION_JSON).body(Map.of("channel", channel, "data", event)).retrieve().toBodilessEntity();
    }
}
