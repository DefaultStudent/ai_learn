# 日志服务模块

`SystemLogService` 同时输出 Log4j2 和数据库审计日志。关键业务动作必须调用该服务，日志入库使用独立事务；禁止记录密码、Token、完整身份证号等敏感数据。实体表为 `mes_system_log`，由 Hibernate 在学习环境自动创建。
