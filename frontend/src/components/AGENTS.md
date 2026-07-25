# 可复用组件层

组件负责页面展示和用户交互，通过 `src/api` 调用后端 REST API。组件不直接依赖 Axios 实例，不在模板内拼接接口 URL；复杂业务流程应拆到 composable 或 store。

组件 Props、Emits、事件处理器、列表项和异步状态都必须有明确类型，禁止任何形式的 `any`。样式使用 Tailwind 静态类或明确的 class 映射，例如 `{ 'bg-emerald-500': online }`；禁止调用 `__VLS_asFunctionalElement` 或其他 `__VLS_*` 内部函数来设置样式或操纵 DOM。
