# order 领域模块

`ProductionOrder` 是生产工单实体，Hibernate 会根据 JPA 注解自动创建 `mes_production_order` 表。状态值当前使用字符串，后续可演进为枚举或状态机。
