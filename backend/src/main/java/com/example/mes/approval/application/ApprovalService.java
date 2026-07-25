package com.example.mes.approval.application;

import com.example.mes.approval.domain.ApprovalRequest;
import com.example.mes.approval.domain.ApprovalStatus;
import com.example.mes.approval.infrastructure.ApprovalRequestRepository;
import com.example.mes.approval.infrastructure.ApprovalEventPublisher;
import com.example.mes.common.exception.ResourceNotFoundException;
import com.example.mes.common.logging.AuditLog;
import com.example.mes.device.api.DeviceController.DeviceRequest;
import com.example.mes.device.application.DeviceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 审批申请应用服务，负责保存申请并执行已批准的业务变更。 */
@Service
public class ApprovalService {
    private final ApprovalRequestRepository repository;
    private final DeviceService deviceService;
    private final ObjectMapper objectMapper;
    private final ApprovalEventPublisher eventPublisher;

    /**
     * @param repository 审批申请持久化仓库
     * @param deviceService 设备业务服务
     * @param objectMapper JSON 数据解析器
     */
    public ApprovalService(ApprovalRequestRepository repository, DeviceService deviceService, ObjectMapper objectMapper, ApprovalEventPublisher eventPublisher) {
        this.repository = repository;
        this.deviceService = deviceService;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * @param requester 申请人账号
     * @param module 业务模块
     * @param action 业务操作
     * @param payload 待审批数据
     * @return 创建的审批申请
     */
    @Transactional
    @AuditLog(module = "approval", action = "SUBMIT")
    public ApprovalRequest submit(String requester, String module, String action, String payload) {
        ApprovalRequest request = repository.save(new ApprovalRequest(requester, module, action, payload));
        eventPublisher.publish(new ApprovalEvent(request.getId(), requester, module, action, request.getStatus().name()));
        return request;
    }

    /** @return 全部审批申请 */
    @Transactional(readOnly = true)
    public List<ApprovalRequest> list() { return repository.findAll(); }

    /**
     * @param requester 当前申请人账号
     * @return 当前申请人的申请记录
     */
    @Transactional(readOnly = true)
    public List<ApprovalRequest> listMine(String requester) { return repository.findByRequesterOrderByCreatedAtDesc(requester); }

    /**
     * @param id 申请主键
     * @param reviewer 审批人账号
     * @param approved 是否批准
     * @param remark 审批意见
     * @return 更新后的审批申请
     * @throws ResourceNotFoundException 申请不存在
     * @throws IllegalArgumentException 申请数据无效
     */
    @Transactional
    @AuditLog(module = "approval", action = "REVIEW")
    public ApprovalRequest review(Long id, String reviewer, boolean approved, String remark) {
        ApprovalRequest request = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("审批请求不存在: " + id));
        if (request.getStatus() != ApprovalStatus.PENDING) throw new IllegalArgumentException("审批申请已经处理");
        if (approved) applyApprovedChange(request);
        request.review(reviewer, approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED, remark);
        eventPublisher.publish(new ApprovalEvent(request.getId(), request.getRequester(), request.getModule(), request.getAction(), request.getStatus().name()));
        return request;
    }

    /**
     * 执行已批准的设备变更。
     * @param request 已批准的审批申请
     * @throws IllegalArgumentException 模块、操作或申请数据不受支持
     */
    private void applyApprovedChange(ApprovalRequest request) {
        if (!"device".equals(request.getModule())) throw new IllegalArgumentException("暂不支持的审批模块: " + request.getModule());
        try {
            JsonNode payload = objectMapper.readTree(request.getPayload());
            switch (request.getAction()) {
                case "CREATE" -> deviceService.create(new DeviceRequest(payload.path("code").asText(), payload.path("name").asText()));
                case "UPDATE" -> deviceService.update(payload.path("id").asLong(), new DeviceRequest(payload.path("code").asText(), payload.path("name").asText()));
                case "UPDATE_STATUS" -> deviceService.changeStatus(payload.path("id").asLong(), payload.path("status").asText());
                case "DELETE" -> deviceService.delete(payload.path("id").asLong());
                default -> throw new IllegalArgumentException("暂不支持的审批操作: " + request.getAction());
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("审批申请数据格式错误", exception);
        }
    }
}
