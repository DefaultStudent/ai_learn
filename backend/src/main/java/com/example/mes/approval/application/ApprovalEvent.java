package com.example.mes.approval.application;

/** 审批状态变更事件，供 RabbitMQ 和实时通知网关使用。 */
public record ApprovalEvent(Long id, String requester, String module, String action, String status) {
}
