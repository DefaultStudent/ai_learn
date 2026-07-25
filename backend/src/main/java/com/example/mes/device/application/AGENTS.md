# device application 层

应用服务的每个用例方法都必须说明参数、返回值、事务边界和可能的业务异常；禁止只写方法名而不解释参数语义。

应用服务负责设备用例、事务边界、分页编排和领域对象变更。Controller 不应直接调用 Repository；后续接入 Redis 缓存或 RabbitMQ 事件时也应由本层编排。
