# 前端开发规范

本项目使用 Vue 3 Composition API、TypeScript、Vue Router、Pinia、Tailwind CSS 和 Nuxt UI。页面负责展示与交互，接口请求集中放在 src/api，跨页面状态使用 Pinia 或 composable。

## UI 设计基准

页面设计参考 [Nuxt Dashboard Template](https://dashboard-template.nuxt.dev/) 的 dashboard 结构：

- 使用左侧导航、顶部工具栏和主内容区的清晰层级。
- 优先采用紧凑的 KPI 卡片、分组内容卡片和可扫描的数据表格。
- 使用浅色、低饱和、克制的蓝灰色或青绿色作为界面基础色。
- 通过留白、圆角、柔和阴影、轻微背景色差建立层次，不依赖高饱和渐变。
- 避免霓虹色、紫粉 AI 渐变、深色模式和生硬的大幅动画。

## 强制边界规范

以下规则是硬性要求，新增或修改页面、组件时必须遵守：

- 页面内部的表格和卡片绝对不能使用黑色、纯黑色或接近黑色的分割线来区分边界。
- 卡片边界必须使用低饱和蓝灰色、雾灰色或半透明边框，并配合柔和阴影或背景色差。
- 表格的表头、行和外框必须使用蓝灰色层级区分；允许使用极浅的隔行底色，但不得使用黑色横线。
- 禁止直接使用 border-black、border-gray-900、border-slate-900、#000 或同等深色边框作为 UI 分隔线。
- Nuxt UI 的 UCard、UTable 或其他带默认边框的组件必须通过 class、ui 配置或全局 slot 样式覆盖默认边框，不能假设默认主题符合规范。
- 分隔线应优先被弱化为背景色差、阴影、内阴影或间距；确需边框时使用柔和色值，不使用硬黑线。
- 所有页面完成后必须进行视觉检查：卡片相对页面背景要有清晰但柔和的对比，表格内容可读且没有黑色线框感。
- 所有界面图标必须使用 Google Material Symbols Rounded 图标库，统一通过 material-symbols-rounded 图标字体或封装组件渲染。
- 禁止使用键盘输入的 Unicode 符号充当图标，包括箭头、勾号、方块、齿轮、房屋、圆点和排序箭头。
- 图标名称应使用 Material Symbols 的 ligature 名称，例如 dashboard、settings、expand_more、left_panel_close、sort。

## 代码约束

- 禁止使用 any，未知数据使用 unknown 并在边界处完成类型收窄。
- 保持 tsconfig 的 strict 检查。
- 样式使用静态 Tailwind class、明确的 :class 映射或 :style，不要拼接不可审查的 class。
- 组件命名使用 PascalCase；页面业务状态放到 composable 或 store。
- 提交前运行 npm run build 或对应的 Docker 构建任务。
