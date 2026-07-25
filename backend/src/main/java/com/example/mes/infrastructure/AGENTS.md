# 基础设施层

Repository、消息发布器、缓存适配器和 Netty 回调方法必须有参数文档；查询参数、分页参数、消息体和连接上下文都要在 `@param` 中说明来源和用途。

基础设施日志使用 Log4j2，控制台和滚动文件由 `log4j2-spring.xml` 管理；数据库审计日志统一通过公共 `SystemLogService` 写入。

负责 PostgreSQL/JPA、Redis、RabbitMQ、Netty 等外部技术适配。配置使用 `application.yml` 和环境变量，不把密码提交到源码。
