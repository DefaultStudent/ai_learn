# device 模块

设备模块所有 Controller、Service、Repository、实体行为和 Netty 回调都必须为参数列表逐项提供 Javadoc `@param`；新增设备状态、遥测协议或分页参数时同步更新参数说明。

设备模块负责设备主数据、状态、心跳与遥测。HTTP 管理接口走 Spring MVC，设备长连接遥测走 Netty；遥测入站后应发布 RabbitMQ 事件，不应在 Netty handler 中直接写数据库。
