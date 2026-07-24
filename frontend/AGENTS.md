# 前台目录约定

前台使用 Vue 3 Composition API + TypeScript + Tailwind CSS。页面只负责展示与交互，接口请求集中放在 `src/api`，跨页面状态使用 Pinia。不要在组件内直接拼接后端 URL；新增页面需同时补充路由和模块说明文档。
