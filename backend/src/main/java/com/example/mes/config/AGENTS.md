# 配置模块

配置 Bean 工厂方法即使由 Spring 调用，也必须说明参数来源、Bean 用途和返回对象；方法参数必须有 `@param`。

集中管理 MQ 队列、Redis 缓存、数据库和 Netty 参数。新增配置优先绑定类型化 `@ConfigurationProperties`，并为每项提供环境变量默认值。
