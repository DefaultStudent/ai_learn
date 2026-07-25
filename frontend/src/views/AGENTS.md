# Views 目录约定

- View 负责页面组合，不直接承载跨页面状态。
- 页面业务状态应移动到 composable 或 Pinia store。
- View 优先复用 `components/` 中的业务组件。
- 页面必须通过路由懒加载，避免在 `App.vue` 静态引入业务页面。
