# src UI 实施规范

本目录中的页面和组件必须遵循前端根目录 AGENTS.md 的视觉约束，并以 Nuxt Dashboard Template 为布局参考：
https://dashboard-template.nuxt.dev/

## 强制要求

- 表格和卡片的内部边界严禁使用黑色或接近黑色的分割线。
- 禁止使用 border-black、border-gray-900、border-slate-900、#000 等深色边框。
- UCard、UTable 和自定义容器必须使用柔和蓝灰/雾灰边框、背景色差、留白或阴影建立边界。
- Nuxt UI 的默认 border-default、data-slot 边框和主题边框必须在组件样式中确认并覆盖。
- 页面应保持浅色 dashboard 风格：左侧导航、顶部上下文、摘要卡片和可扫描表格，避免霓虹色、紫粉渐变、深色模式和生硬动画。
- 所有图标必须来自 Google Material Symbols Rounded；不得在模板或脚本中直接输出 Unicode 图标字符。
- 导航、指标卡、菜单、折叠按钮和排序提示统一使用 Material Symbols ligature 名称。

修改 UI 后必须检查白色页面背景上的卡片对比度，以及表格是否仍出现黑色横线或线框。
