package com.example.mes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MesApplication {
    /**
     * 应用入口：启动 Spring MVC、JPA、Redis、RabbitMQ 和 Netty 组件。
     * @param args JVM 启动参数
     */
    public static void main(String[] args) { SpringApplication.run(MesApplication.class, args); }
}
