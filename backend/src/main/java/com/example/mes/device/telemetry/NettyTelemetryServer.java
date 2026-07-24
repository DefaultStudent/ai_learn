package com.example.mes.device.telemetry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class NettyTelemetryServer {
    /** 设备 TCP 遥测端口，默认由 application.yml 配置为 9000。 */
    @Value("${mes.netty.port:9000}") private int port;
    private EventLoopGroup boss;
    private EventLoopGroup worker;

    /** Spring 初始化后启动 Netty；当前 handler 只返回 ACK，作为协议接入学习示例。 */
    @PostConstruct void start() {
        boss = new NioEventLoopGroup(1); worker = new NioEventLoopGroup();
        new ServerBootstrap().group(boss, worker).channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<Channel>() { protected void initChannel(Channel ch) { ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() { protected void channelRead0(ChannelHandlerContext ctx, String msg) { ctx.writeAndFlush("ACK\\n"); } }); } })
            .bind(port).addListener((ChannelFutureListener) f -> { if (!f.isSuccess()) f.cause().printStackTrace(); });
    }

    /** Spring 关闭时释放 Netty 线程池，避免端口和线程泄漏。 */
    @PreDestroy void stop() { if (boss != null) boss.shutdownGracefully(); if (worker != null) worker.shutdownGracefully(); }
}
