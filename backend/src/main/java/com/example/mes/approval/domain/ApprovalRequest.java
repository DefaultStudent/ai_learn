package com.example.mes.approval.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/** 部门管理者提交的业务变更审批请求。 */
@Entity
@Table(name = "mes_approval_request")
public class ApprovalRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 64) private String requester;
    @Column(nullable = false, length = 64) private String module;
    @Column(nullable = false, length = 64) private String action;
    @Column(nullable = false, length = 4000) private String payload;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private ApprovalStatus status = ApprovalStatus.PENDING;
    @Column(length = 64) private String reviewer;
    @Column(length = 500) private String reviewRemark;
    @Column(nullable = false) private OffsetDateTime createdAt;
    private OffsetDateTime reviewedAt;
    /** JPA 默认构造函数，仅供持久化框架创建实体。 */
    protected ApprovalRequest() {
    }
    /** @param requester 发起人 @param module 业务模块 @param action 业务动作 @param payload 待审批数据 */
    public ApprovalRequest(String requester, String module, String action, String payload) {
        this.requester = requester;
        this.module = module;
        this.action = action;
        this.payload = payload;
    }

    /** 在首次保存实体时补充创建时间。 */
    @PrePersist
    void beforePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
    /** @param reviewer 审批人 @param status 审批结果 @param remark 审批意见 */
    public void review(String reviewer, ApprovalStatus status, String remark) {
        this.reviewer = reviewer;
        this.status = status;
        this.reviewRemark = remark;
        this.reviewedAt = OffsetDateTime.now();
    }

    /** @return 审批申请主键 */
    public Long getId() {
        return id;
    }

    /** @return 申请人账号 */
    public String getRequester() {
        return requester;
    }

    /** @return 业务模块名称 */
    public String getModule() {
        return module;
    }

    /** @return 申请动作 */
    public String getAction() {
        return action;
    }

    /** @return 审批请求载荷 */
    public String getPayload() {
        return payload;
    }

    /** @return 当前审批状态 */
    public ApprovalStatus getStatus() {
        return status;
    }

    /** @return 审批人账号 */
    public String getReviewer() {
        return reviewer;
    }

    /** @return 审批意见 */
    public String getReviewRemark() {
        return reviewRemark;
    }

    /** @return 申请创建时间 */
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    /** @return 审批完成时间 */
    public OffsetDateTime getReviewedAt() {
        return reviewedAt;
    }
}
