# dashboard API 子层

Controller 方法、Summary DTO 构造器及其字段必须有参数文档；接口文档要说明分页、统计口径和响应结构。

放生产总览的 REST Controller、请求响应 DTO 和参数校验。只依赖 dashboard 应用服务；当前演示接口内置样例数据，接入真实数据时应迁移到 application 层。
