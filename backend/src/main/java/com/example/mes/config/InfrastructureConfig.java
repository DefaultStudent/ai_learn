package com.example.mes.config;

import org.springframework.amqp.core.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {
    private static final Logger log = LogManager.getLogger(InfrastructureConfig.class);
    /** RabbitMQ 持久队列：用于后续发布生产进度和设备遥测事件。 */
    /**
     * @return 持久化的生产事件队列
     */
    @Bean Queue productionEventsQueue() {
        Queue queue = new Queue("mes.production.events", true);
        log.info("RabbitMQ queue configured: {}", queue.getName());
        return queue;
    }
}
