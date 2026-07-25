package com.example.mes.user.api;

import com.example.mes.user.domain.AppUser;
import com.example.mes.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 用户模块请求和响应 DTO。 */
public final class UserDtos {
    private UserDtos() {}

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record LoginResponse(Long id, String username, String displayName, UserRole role, String department) {
        public static LoginResponse from(AppUser user) { return new LoginResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole(), user.getDepartment()); }
        /** @param id 用户主键 @param username 登录账号 @param displayName 显示名称 @param role 系统角色 @param department 所属部门 @return 登录用户响应 */
        public static LoginResponse of(Long id, String username, String displayName, UserRole role, String department) { return new LoginResponse(id, username, displayName, role, department); }
    }
    public record UserRequest(@NotBlank @Size(max = 64) String username, @Size(min = 6, max = 128) String password,
                              @NotBlank @Size(max = 128) String displayName, @NotNull UserRole role, @Size(max = 64) String department) {}
}
