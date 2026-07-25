package com.example.mes.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/** 系统用户实体，密码只保存 BCrypt 哈希。 */
@Entity
@Table(name = "mes_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 128)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserRole role;

    @Column(length = 64)
    private String department;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    /** JPA 默认构造函数，仅供持久化框架创建实体。 */
    protected AppUser() {
    }

    /**
     * 创建系统用户。
     *
     * @param username 登录账号
     * @param passwordHash BCrypt 密码哈希
     * @param displayName 用户显示名称
     * @param role 用户角色
     * @param department 所属部门
     */
    public AppUser(
            String username,
            String passwordHash,
            String displayName,
            UserRole role,
            String department) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.role = role;
        this.department = department;
    }

    /** 在首次保存用户实体时写入创建时间。 */
    @PrePersist
    void beforePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    /** @return 用户主键 */
    public Long getId() {
        return id;
    }

    /** @return 登录账号 */
    public String getUsername() {
        return username;
    }

    /** @return BCrypt 密码哈希 */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** @return 显示名称 */
    public String getDisplayName() {
        return displayName;
    }

    /** @return 用户角色 */
    public UserRole getRole() {
        return role;
    }

    /** @return 所属部门 */
    public String getDepartment() {
        return department;
    }

    /** @return 用户是否启用 */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 更新用户资料和权限。
     *
     * @param passwordHash 新的 BCrypt 密码哈希；为空时保留原密码
     * @param displayName 新的显示名称
     * @param role 新的用户角色
     * @param department 新的所属部门
     * @return 无返回值
     */
    public void update(
            String passwordHash,
            String displayName,
            UserRole role,
            String department) {
        if (passwordHash != null && !passwordHash.isBlank()) {
            this.passwordHash = passwordHash;
        }
        this.displayName = displayName;
        this.role = role;
        this.department = department;
    }

    /**
     * 修改用户启用状态。
     *
     * @param enabled 用户是否允许登录
     * @return 无返回值
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
