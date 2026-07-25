# 可复用组件规范

组件负责页面展示和用户交互，通过 src/api 调用后端 REST API。组件 Props、Emits、列表项和异步状态必须有明确类型，禁止任何形式的 any。

## Dashboard 视觉规范

组件布局参考 [Nuxt Dashboard Template](https://dashboard-template.nuxt.dev/)：

- 采用清晰的 dashboard 分区：导航、顶部上下文、KPI/摘要卡片、数据表格和操作区。
- 卡片应使用柔和圆角、浅蓝灰或冷白背景、轻微阴影和明确的内外间距。
- 表格应保持简洁、可扫描；表头和隔行底色可以提供层次，但不要堆叠厚重装饰。

## 强制边界规范

- UCard、UTable 及自定义卡片、表格中，严禁使用黑色或接近黑色的分割线。
- 禁止 border-black、border-gray-900、border-slate-900、#000 或任何等效深色边框。
- 卡片边界使用柔和蓝灰/雾灰边框，配合阴影或背景色差；不要只依赖一条硬边框。
- 表格外框、表头底线、单元格分隔线必须覆盖为低对比蓝灰色；优先使用浅色隔行底色和留白。
- 使用 Nuxt UI 组件时，必须检查其默认 data-slot、border-default 和主题边框是否被覆盖。
- 新组件完成后要检查它放在白色页面背景上是否有足够的柔和对比，同时确保不会产生黑色线框。
- 图标只能使用 Google Material Symbols Rounded，不得使用键盘输入的箭头、勾号、方块、齿轮或其他 Unicode 符号替代图标。
- 需要图标时使用 material-symbols-rounded 类和 Material Symbols ligature 名称，或使用统一的图标封装组件。

弹窗统一使用 ModalDialog，短时反馈统一使用 ToastMessage；不要在业务组件中重复实现遮罩、计时器或气泡样式。
