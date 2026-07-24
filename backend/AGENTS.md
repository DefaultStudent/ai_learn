# 后端目录约定

后端使用 Java 17、Spring Boot、Maven 和经典 MVC。包按业务模块组织，模块内部遵循 `api -> application -> domain -> infrastructure` 的依赖方向。Controller 不直接访问 Repository；跨模块通信优先使用应用服务或领域事件。
