# 配置文件

使用 YAML；敏感值通过环境变量注入。`application.yml` 只保留本地开发默认值和配置键说明，不提交真实凭据。

业务配置继续使用 `application.yml`；Log4j2 的输出器、滚动策略和 UTF-8 格式由专用 `log4j2-spring.xml` 管理。
