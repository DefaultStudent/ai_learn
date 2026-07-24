package com.example.mes.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {
    /** RabbitMQ 持久队列：用于后续发布生产进度和设备遥测事件。 */
    @Bean Queue productionEventsQueue() { return new Queue("mes.production.events", true); }
}
