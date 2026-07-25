# device infrastructure 层

Repository 查询方法必须为关键字、状态、分页对象和返回结果补充参数说明；派生查询名称不能替代 Javadoc。

`DeviceRepository` 是设备模块的 PostgreSQL/JPA 适配器。查询规则集中在 Repository，禁止把数据库查询写进 Controller 或 Netty handler。
