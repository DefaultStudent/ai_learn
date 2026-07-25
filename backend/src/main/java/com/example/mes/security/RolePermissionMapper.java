package com.example.mes.security;

import com.example.mes.user.domain.UserRole;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/** 将后端用户角色映射为权限，并生成 Spring Security 授权对象。 */
public final class RolePermissionMapper {
    private RolePermissionMapper() {
    }

    /**
     * 获取角色对应的权限集合。
     *
     * @param role 用户角色
     * @return 角色拥有的权限集合
     */
    public static Set<Permission> permissionsFor(UserRole role) {
        return switch (role) {
            case SYSTEM_ADMIN -> EnumSet.allOf(Permission.class);
            case ADMIN -> EnumSet.of(
                    Permission.DEVICE_VIEW,
                    Permission.DEVICE_MANAGE,
                    Permission.PRODUCTION_VIEW,
                    Permission.APPROVAL_SUBMIT,
                    Permission.APPROVAL_REVIEW);
            case PRODUCTION_MANAGER -> EnumSet.of(
                    Permission.PRODUCTION_VIEW,
                    Permission.APPROVAL_SUBMIT,
                    Permission.APPROVAL_MINE);
            case DEVICE_MANAGER -> EnumSet.of(
                    Permission.DEVICE_VIEW,
                    Permission.DEVICE_PROPOSE,
                    Permission.APPROVAL_SUBMIT,
                    Permission.APPROVAL_MINE);
            case PRODUCTION_USER -> EnumSet.of(Permission.PRODUCTION_VIEW);
            case DEVICE_USER -> EnumSet.of(Permission.DEVICE_VIEW);
        };
    }

    /**
     * 获取角色返回给前端的权限名称列表。
     *
     * @param role 用户角色
     * @return 权限名称列表
     */
    public static List<String> permissionNames(UserRole role) {
        return permissionsFor(role).stream().map(Enum::name).sorted().toList();
    }

    /**
     * 创建 Spring Security 权限集合，同时保留角色信息供审计和兼容逻辑使用。
     *
     * @param role 用户角色
     * @return Spring Security 授权集合
     */
    public static List<GrantedAuthority> authoritiesFor(UserRole role) {
        List<GrantedAuthority> authorities = permissionsFor(role).stream()
                .map(permission -> new SimpleGrantedAuthority("PERM_" + permission.name()))
                .map(GrantedAuthority.class::cast)
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return authorities;
    }
}
