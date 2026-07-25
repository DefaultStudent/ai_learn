# order 领域模块

生产工单实体构造器和状态变更方法必须为每个参数写 `@param`，并说明数量、工单号和状态的约束。

`ProductionOrder` 是生产工单实体，Hibernate 会根据 JPA 注解自动创建 `mes_production_order` 表。状态值当前使用字符串，后续可演进为枚举或状态机。
