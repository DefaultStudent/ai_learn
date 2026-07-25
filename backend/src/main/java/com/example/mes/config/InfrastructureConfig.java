package com.example.mes.config;

import com.example.mes.approval.infrastructure.ApprovalEventPublisher;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

/** RabbitMQ 交换机、队列和消息序列化配置。 */
@Configuration
public class InfrastructureConfig {
    /** @return 生产事件队列 */
    @Bean
    Queue productionEventsQueue() {
        // 生产事件队列持久化保存，避免服务重启时丢失消息。
        return new Queue("mes.production.events", true);
    }

    /** @return 审批事件交换机 */
    @Bean
    DirectExchange approvalEventsExchange() {
        // 使用直连交换机，让审批路由键精确绑定到通知队列。
        return new DirectExchange(ApprovalEventPublisher.EXCHANGE, true, false);
    }

    /** @return Centrifugo 通知队列 */
    @Bean
    Queue approvalNotificationQueue() {
        return new Queue("mes.approval.notifications", true);
    }

    /** @param approvalNotificationQueue 审批通知队列 @param approvalEventsExchange 审批事件交换机 @return 审批事件绑定 */
    @Bean
    Binding approvalNotificationBinding(
            @Qualifier("approvalNotificationQueue") Queue approvalNotificationQueue,
            @Qualifier("approvalEventsExchange") DirectExchange approvalEventsExchange) {
        return BindingBuilder.bind(approvalNotificationQueue)
                .to(approvalEventsExchange)
                .with(ApprovalEventPublisher.ROUTING_KEY);
    }

    /** @return RabbitMQ JSON 消息转换器 */
    @Bean
    Jackson2JsonMessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /** @param connectionFactory RabbitMQ 连接工厂 @param converter JSON 消息转换器 @return 配置后的 RabbitTemplate */
    @Bean
    RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
