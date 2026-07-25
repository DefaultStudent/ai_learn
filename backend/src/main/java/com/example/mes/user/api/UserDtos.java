package com.example.mes.user.api;

import com.example.mes.security.RolePermissionMapper;
import com.example.mes.user.domain.AppUser;
import com.example.mes.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/** 用户模块请求和响应 DTO。 */
public final class UserDtos {
    private UserDtos() {
    }

    /** 登录请求参数。 */
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    /** 当前登录用户信息和后端计算出的权限列表。 */
    public record LoginResponse(
            Long id,
            String username,
            String displayName,
            UserRole role,
            String department,
            List<String> permissions) {
        /**
         * 将本地用户实体转换为登录响应。
         *
         * @param user 本地用户实体
         * @return 包含后端权限列表的用户响应
         */
        public static LoginResponse from(AppUser user) {
            return new LoginResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    user.getRole(),
                    user.getDepartment(),
                    RolePermissionMapper.permissionNames(user.getRole()));
        }

        /**
         * 创建外部权限服务返回的用户响应。
         *
         * @param id 用户主键
         * @param username 登录账号
         * @param displayName 显示名称
         * @param role 用户角色
         * @param department 所属部门
         * @param permissions 外部权限服务返回的权限
         * @return 用户响应
         */
        public static LoginResponse of(
                Long id,
                String username,
                String displayName,
                UserRole role,
                String department,
                List<String> permissions) {
            List<String> resolvedPermissions = permissions == null
                    ? RolePermissionMapper.permissionNames(role)
                    : List.copyOf(permissions);
            return new LoginResponse(id, username, displayName, role, department, resolvedPermissions);
        }
    }

    /** 用户创建或更新请求。 */
    public record UserRequest(
            @NotBlank @Size(max = 64) String username,
            @Size(min = 6, max = 128) String password,
            @NotBlank @Size(max = 128) String displayName,
            @NotNull UserRole role,
            @Size(max = 64) String department) {
    }
}
