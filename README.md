# MES Learning Project

一个用于学习企业级 Web 开发的轻量 MES（制造执行系统）项目。当前版本以“生产总览 + 设备接入 + 生产工单领域模型”为主线，代码保持简单，方便逐步学习 MVC、JPA、缓存、消息队列和反向代理。

## 技术栈

- 前台：Vue 3、Vite、TypeScript、Tailwind CSS、Pinia、Vue Router
- 后台：Spring Boot 3、Spring MVC、Spring Data JPA、Maven、PostgreSQL
- 基础设施：Redis、RabbitMQ、Nginx
- 设备接入：Netty TCP telemetry server
- API：RESTful JSON API，统一返回分页与错误结构

## 快速开始

1. 确保本机 PostgreSQL 已创建数据库 `mes`，并确认用户、密码与 `backend/src/main/resources/application.yml` 默认值一致。
2. 启动 Docker 中间件：`docker compose -f deploy/docker-compose.yml up -d`
3. 启动后端：`cd backend; mvn spring-boot:run`
4. 前台由 Docker 中的 Node.js 容器自动启动，无需本机安装 Node.js。
5. 打开 <http://localhost>

在 VS Code 中请使用“终端 → 运行任务 → MES: Start Backend”，任务会自动以 `backend` 作为工作目录。前端和中间件由 Docker Compose 启动。修改终端设置后请关闭旧终端，并新建一个终端。

默认 PostgreSQL 地址为 `localhost:5432`，API 地址为 `http://localhost:8080/api`，Netty 遥测端口为 `9000`。

## 服务与端口

| 服务 | 访问地址/端口 | 运行方式 |
| --- | --- | --- |
| Nginx 统一入口 | <http://localhost> / `80` | Docker |
| Vue/Vite 前台 | <http://localhost:5173> | Docker 中的 Node.js |
| Spring Boot API | <http://localhost:8080> | 本机 Java + Maven |
| Netty 遥测接入 | `9000` | Spring Boot 内嵌启动 |
| RabbitMQ 管理台 | <http://localhost:15672> | Docker，账号密码 閫氳繃 MQ_USER/MQ_PASSWORD 鐜鍙橀噺閰嶇疆 |
| Redis | `6379` | Docker |
| PostgreSQL | `5432` | 本机安装 |

## 启动顺序

1. 本机 PostgreSQL 创建数据库 `mes`，并让 `mes` 用户拥有数据库权限。
2. 确认 Docker Desktop 正在运行。
3. 执行 `docker compose -f deploy/docker-compose.yml up -d`，它会启动 Node.js 前台、Redis、RabbitMQ 和 Nginx。
4. 在 `backend` 目录执行 `mvn spring-boot:run`。
5. 访问 <http://localhost>；接口可用 <http://localhost:8080/api/dashboard/summary> 验证。

如果访问 `localhost` 出现 502，先检查 `http://localhost:8080/api/dashboard/summary` 和 `http://localhost:5173` 是否可访问，再查看 `docker compose -f deploy/docker-compose.yml logs nginx frontend`。

后端使用 Hibernate `ddl-auto: update`，首次启动会根据 `@Entity` 自动创建 `mes_device`、`mes_production_order` 等表，并保留已有数据。生产环境应改用 Flyway 或 Liquibase。

## 后台日志

后端使用 Log4j2 输出 UTF-8 控制台日志和滚动文件日志，文件默认写入 `backend/logs/mes-backend.log`。设备创建、更新、状态变更和删除等关键操作会通过 `SystemLogService` 写入 PostgreSQL 的 `mes_system_log` 表；该日志使用独立事务，业务回滚时仍保留审计记录。日志中禁止写入密码、Token 等敏感信息。

## 目录约定

每个业务层与模块都带有自己的 `AGENTS.md`，其中记录职责、依赖方向、接口约定和扩展规则。修改代码前请先阅读所在目录及父目录的文档。

## 自动建表与数据库演进

后端使用 Hibernate `ddl-auto: update`。启动时会扫描 `@Entity`，自动创建缺失表，例如 `mes_device` 和 `mes_production_order`，并尽量保留已有数据。新增实体时需要补充字段注释、表名和对应模块文档。

该配置适合本地学习。生产环境应关闭自动更新，改用 Flyway 或 Liquibase 管理可审计的数据库迁移。

## 代码注释约定

- Controller 注释接口用途、HTTP 路径和返回结构。
- Application/Infrastructure 注释事务编排、外部依赖和生命周期。
- Entity 注释表用途、字段含义和自动建表行为。
- Vue/API 注释页面职责、请求地址和数据来源。
- YAML、Compose、Nginx 注释端口、服务依赖和宿主机/容器边界。
