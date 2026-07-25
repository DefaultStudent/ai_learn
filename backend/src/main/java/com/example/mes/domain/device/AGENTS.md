# device 领域模块

设备实体构造器和 `update`、`changeStatus` 等领域行为必须为每个参数写 `@param`，并说明状态值的业务含义。

`Device` 是设备主数据实体，Hibernate 会根据 JPA 注解自动创建 `mes_device` 表。新增字段时遵守数据库兼容性原则，生产环境使用 Flyway/Liquibase 管理迁移。
