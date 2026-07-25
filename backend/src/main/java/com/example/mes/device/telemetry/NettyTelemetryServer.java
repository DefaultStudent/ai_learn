package com.example.mes.device.telemetry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class NettyTelemetryServer {
    private static final Logger log = LogManager.getLogger(NettyTelemetryServer.class);
    /** 设备 TCP 遥测端口，默认由 application.yml 配置为 9000。 */
    @Value("${mes.netty.port:9000}")
    private int port;
    private EventLoopGroup boss;
    private EventLoopGroup worker;

    /** Spring 初始化后启动 Netty；当前 handler 只返回 ACK，作为协议接入学习示例。 */
    @PostConstruct
    void start() {
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup();
        new ServerBootstrap().group(boss, worker).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    /**
                     * 初始化设备连接的处理器链。
                     * @param ch 新建立的 Netty 设备连接
                     */
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            /**
                             * 接收遥测消息并返回学习用 ACK。
                             * @param ctx 当前设备连接上下文
                             * @param msg 设备发送的遥测消息
                             */
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                ctx.writeAndFlush("ACK\\n");
                            }
                        });
                    }
                })
                .bind(port).addListener((ChannelFutureListener) f -> {
                    // f 表示 Netty 端口绑定结果；失败时输出原因便于启动排查。
                    if (!f.isSuccess()) {
                        log.error("Netty telemetry server failed to bind port {}", port, f.cause());
                    } else {
                        log.info("Netty telemetry server started on port {}", port);
                    }
                });
    }

    /** Spring 关闭时释放 Netty 线程池，避免端口和线程泄漏。 */
    @PreDestroy
    void stop() {
        if (boss != null)
            boss.shutdownGracefully();
        if (worker != null)
            worker.shutdownGracefully();
    }
}
