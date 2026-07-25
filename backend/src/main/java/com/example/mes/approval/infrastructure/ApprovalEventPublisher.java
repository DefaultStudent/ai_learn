package com.example.mes.approval.infrastructure;

import com.example.mes.approval.application.ApprovalEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** 将审批事件发布到 RabbitMQ。 */
@Component
public class ApprovalEventPublisher {
    public static final String EXCHANGE = "mes.approval.events";
    public static final String ROUTING_KEY = "approval.changed";
    private final RabbitTemplate rabbitTemplate;

    /** @param rabbitTemplate RabbitMQ 操作模板 */
    public ApprovalEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /** @param event 审批状态变更事件 */
    public void publish(ApprovalEvent event) {
        // 业务事件通过 RabbitMQ 异步转交给实时通知消费者。
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }
}
