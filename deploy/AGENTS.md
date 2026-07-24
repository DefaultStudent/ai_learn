# 部署目录

Docker Compose 仅编排本地学习环境，当前负责 Node.js/Vite 前端、Redis、RabbitMQ、Nginx；PostgreSQL 和 Spring Boot 使用宿主机安装/运行版本。Nginx 将 `/api/` 反向代理到 Spring Boot，将其余请求转发到 `frontend:5173`；生产环境应替换为构建后的静态文件和正式域名。
