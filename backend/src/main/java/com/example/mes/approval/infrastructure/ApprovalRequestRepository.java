package com.example.mes.approval.infrastructure;

import com.example.mes.approval.domain.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** 审批请求持久化接口。 */
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {
    /** @param requester 申请人账号 @return 该申请人的申请记录 */
    List<ApprovalRequest> findByRequesterOrderByCreatedAtDesc(String requester);
}
