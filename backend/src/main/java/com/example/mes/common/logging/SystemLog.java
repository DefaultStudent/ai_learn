package com.example.mes.common.logging;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/** 关键系统操作日志，Hibernate 会自动创建 mes_system_log 表。 */
@Entity
@Table(name = "mes_system_log")
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String level;

    @Column(nullable = false, length = 64)
    private String module;

    @Column(nullable = false, length = 64)
    private String action;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(length = 128)
    private String operatorName;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    protected SystemLog() {}

    /**
     * 创建一条系统日志。
     * @param level 日志级别，例如 INFO、WARN、ERROR
     * @param module 产生日志的模块
     * @param action 业务动作
     * @param message 业务日志内容
     * @param operatorName 操作人，未接入认证时使用 system
     */
    public SystemLog(String level, String module, String action, String message, String operatorName) {
        this.level = level;
        this.module = module;
        this.action = action;
        this.message = message;
        this.operatorName = operatorName;
    }

    /** 在持久化前补充统一时间。 */
    @PrePersist
    void beforePersist() { if (createdAt == null) createdAt = OffsetDateTime.now(); }

    /** @return 日志主键 */
    public Long getId() { return id; }
    /** @return 日志级别 */
    public String getLevel() { return level; }
    /** @return 业务模块 */
    public String getModule() { return module; }
    /** @return 业务动作 */
    public String getAction() { return action; }
    /** @return 日志内容 */
    public String getMessage() { return message; }
    /** @return 操作人 */
    public String getOperatorName() { return operatorName; }
    /** @return 创建时间 */
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
