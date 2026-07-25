# telemetry 子模块

Netty 的 `ChannelHandlerContext`、消息体、连接初始化器和监听器参数必须在方法 Javadoc 或匿名类所在方法说明中标注用途；不得只依赖变量名表达协议含义。

Netty 监听 `${NETTY_PORT:9000}`。生产环境需增加协议编解码、设备认证、心跳超时、限流和优雅关闭。当前 ACK handler 仅用于学习验证端口可用性。
