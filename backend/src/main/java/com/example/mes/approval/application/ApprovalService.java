package com.example.mes.approval.application;

import com.example.mes.approval.domain.ApprovalRequest;
import com.example.mes.approval.domain.ApprovalStatus;
import com.example.mes.approval.infrastructure.ApprovalEventPublisher;
import com.example.mes.approval.infrastructure.ApprovalRequestRepository;
import com.example.mes.common.exception.ResourceNotFoundException;
import com.example.mes.common.logging.AuditLog;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 审批应用服务，只负责审批生命周期和审批事件，不直接依赖具体业务模块。 */
@Service
public class ApprovalService {
    private final ApprovalRequestRepository repository;
    private final List<ApprovalActionExecutor> actionExecutors;
    private final ApprovalEventPublisher eventPublisher;

    /**
     * 创建审批应用服务。
     *
     * @param repository 审批申请仓库
     * @param actionExecutors 审批通过后的业务动作执行器
     * @param eventPublisher 审批事件发布器
     */
    public ApprovalService(
            ApprovalRequestRepository repository,
            List<ApprovalActionExecutor> actionExecutors,
            ApprovalEventPublisher eventPublisher) {
        this.repository = repository;
        this.actionExecutors = actionExecutors;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 创建审批申请并发布待处理事件。
     *
     * @param requester 申请人账号
     * @param module 业务模块
     * @param action 业务动作
     * @param payload 待审批 JSON 数据
     * @return 创建后的审批申请
     */
    @Transactional
    @AuditLog(module = "approval", action = "SUBMIT")
    public ApprovalRequest submit(String requester, String module, String action, String payload) {
        ApprovalRequest request = repository.save(
                new ApprovalRequest(requester, module, action, payload));
        publishEvent(request);
        return request;
    }

    /**
     * 查询全部审批申请。
     *
     * @return 全部审批申请
     */
    @Transactional(readOnly = true)
    public List<ApprovalRequest> list() {
        return repository.findAll();
    }

    /**
     * 查询指定申请人的审批申请。
     *
     * @param requester 申请人账号
     * @return 申请人的审批申请
     */
    @Transactional(readOnly = true)
    public List<ApprovalRequest> listMine(String requester) {
        return repository.findByRequesterOrderByCreatedAtDesc(requester);
    }

    /**
     * 审批申请，并在通过时调用匹配的业务执行器。
     *
     * @param id 审批申请主键
     * @param reviewer 审批人账号
     * @param approved 是否通过
     * @param remark 审批意见
     * @return 更新后的审批申请
     * @throws ResourceNotFoundException 审批申请不存在
     * @throws IllegalArgumentException 申请已处理或没有匹配的执行器
     */
    @Transactional
    @AuditLog(module = "approval", action = "REVIEW")
    public ApprovalRequest review(Long id, String reviewer, boolean approved, String remark) {
        ApprovalRequest request = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("审批申请不存在: " + id));
        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("审批申请已经处理");
        }
        if (approved) {
            applyApprovedChange(request);
        }
        ApprovalStatus targetStatus = approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED;
        request.review(reviewer, targetStatus, remark);
        publishEvent(request);
        return request;
    }

    /**
     * 查找并执行与审批申请匹配的业务执行器。
     *
     * @param request 已通过的审批申请
     * @return 无返回值
     * @throws IllegalArgumentException 没有支持该模块和动作的执行器
     */
    private void applyApprovedChange(ApprovalRequest request) {
        ApprovalActionExecutor executor = actionExecutors.stream()
                .filter(item -> item.supports(request.getModule(), request.getAction()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "不支持的审批动作: " + request.getModule() + "/" + request.getAction()));
        executor.execute(request.getAction(), request.getPayload());
    }

    /**
     * 发布审批状态事件。
     *
     * @param request 审批申请
     * @return 无返回值
     */
    private void publishEvent(ApprovalRequest request) {
        eventPublisher.publish(new ApprovalEvent(
                request.getId(),
                request.getRequester(),
                request.getModule(),
                request.getAction(),
                request.getStatus().name()));
    }
}
