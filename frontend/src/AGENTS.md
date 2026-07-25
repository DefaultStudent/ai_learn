# 前台源码层

`api/` 负责 HTTP 客户端和接口类型，`components/` 放可复用组件，`views/` 放页面，`stores/` 放全局状态。组件命名使用 PascalCase，接口命名与后端资源一致。

TypeScript 必须保持严格类型检查，禁止 `any`。组件模板只能使用 Vue 公共模板语法和 Tailwind 的 `class`、`:class`、`:style`；严禁手写或引用 `__VLS_asFunctionalElement`、`__VLS_*` 等 Volar 内部辅助函数。
