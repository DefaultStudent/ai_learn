# 前台目录约定

前台使用 Vue 3 Composition API + TypeScript + Tailwind CSS。页面只负责展示与交互，接口请求集中放在 `src/api`，跨页面状态使用 Pinia。不要在组件内直接拼接后端 URL；新增页面需同时补充路由和模块说明文档。

## 强制编码约束

- 禁止使用 `any`，包括显式 `any`、`any[]`、`Promise<any>`、`Record<string, any>` 和通过类型断言绕过检查的写法。优先使用明确接口、泛型；确实未知的数据使用 `unknown`，并在边界处完成类型收窄。
- 禁止在业务代码、模板或样式逻辑中使用 `__VLS_*`、`__VLS_asFunctionalElement` 等 Vue/Volar 编译辅助函数。这些名称只可能出现在工具生成的类型检查产物中，不得手写、复制或用来设置样式。
- 样式只能通过模板上的静态 Tailwind class、受控的 class 映射对象、`:class` 或 `:style` 完成；不要通过编译辅助函数、字符串拼接生成不可审查的样式。
- 保持 `tsconfig` 的 `strict`，提交前运行 `npm run build` 或对应 Docker 构建任务。
