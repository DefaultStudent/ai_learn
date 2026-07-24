# PostgreSQL 初始化模块

目录保留 PostgreSQL 初始化脚本示例，但当前 Compose 不启动 PostgreSQL，因为项目使用宿主机 PostgreSQL。正式数据库变更应迁移到 Flyway/Liquibase，避免依赖 `ddl-auto: update`。
