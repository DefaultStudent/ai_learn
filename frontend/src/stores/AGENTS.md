# Stores 目录约定

- Store 只保存跨页面共享状态和后端返回的事实数据。
- 权限必须读取后端返回的 `permissions`，禁止根据角色在前端重新推导权限。
- Store 不得承担页面布局和组件渲染逻辑。
- TypeScript 类型必须显式声明，禁止使用 `any`。
