# 后端目录约定

后端使用 Java 17、Spring Boot、Maven 和经典 MVC。包按业务模块组织，模块内部遵循 `api -> application -> domain -> infrastructure` 的依赖方向。Controller 不直接访问 Repository；跨模块通信优先使用应用服务或领域事件。

## Java 文档强制规范

- 所有构造器、公开方法、受保护方法和具有业务逻辑的私有方法都必须使用 Javadoc。
- Javadoc 必须为参数列表中的每个参数增加 `@param`，参数名必须与签名完全一致；无参数方法不需要 `@param`。
- 有返回值的方法必须增加 `@return`；会抛出业务异常或声明检查异常时必须增加 `@throws`。
- Controller 方法必须说明 HTTP 用途、每个参数和响应；Service 方法必须说明用例、事务边界、每个参数和返回值；Repository 查询方法必须说明查询条件、每个参数和分页含义。
- 构造器参数同样必须注释。重写方法可以使用 `{@inheritDoc}`，但新增参数仍必须在本方法文档中说明。
- Lambda、匿名类和框架回调的参数应通过所在方法或字段注释说明用途；禁止使用无意义的参数名掩盖数据含义。
- 不允许用行尾注释替代参数文档，也不允许为了省略 Javadoc 而把多个参数压缩成无意义的 `Map` 或 `Object`。
