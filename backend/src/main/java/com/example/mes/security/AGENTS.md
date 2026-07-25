# security 模块

使用 Spring Security 服务器会话和 BCrypt 密码编码。登录接口公开，业务 API 必须认证；角色权限通过 `@PreAuthorize` 约束。禁止前端直接提交角色头、在浏览器保存明文密码或使用伪造角色替代后端授权。
