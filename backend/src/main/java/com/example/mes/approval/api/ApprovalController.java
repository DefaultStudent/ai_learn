package com.example.mes.approval.api;

import com.example.mes.approval.application.ApprovalService;
import com.example.mes.approval.domain.ApprovalRequest;
import com.example.mes.common.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 部门变更审批 REST API。 */
@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {
    private final ApprovalService service;

    /**
     * 创建审批控制器。
     *
     * @param service 审批应用服务
     */
    public ApprovalController(ApprovalService service) {
        this.service = service;
    }

    /**
     * 提交部门变更审批申请。
     *
     * @param request 审批申请内容
     * @param authentication 当前登录用户
     * @return 创建后的审批申请
     */
    @PostMapping
    @PreAuthorize("hasAuthority('PERM_APPROVAL_SUBMIT')")
    public ApiResponse<ApprovalRequest> submit(
            @Valid @RequestBody ApprovalSubmitRequest request,
            Authentication authentication) {
        return ApiResponse.ok(service.submit(
                authentication.getName(),
                request.module(),
                request.action(),
                request.payload()));
    }

    /**
     * 查询全部审批申请。
     *
     * @return 全部审批申请
     */
    @GetMapping
    @PreAuthorize("hasAuthority('PERM_APPROVAL_REVIEW')")
    public ApiResponse<List<ApprovalRequest>> list() {
        return ApiResponse.ok(service.list());
    }

    /**
     * 查询当前用户提交的审批申请。
     *
     * @param authentication 当前登录用户
     * @return 当前用户的审批申请
     */
    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<ApprovalRequest>> mine(Authentication authentication) {
        return ApiResponse.ok(service.listMine(authentication.getName()));
    }

    /**
     * 审批申请并触发对应业务变更。
     *
     * @param id 审批申请主键
     * @param request 审批决定和意见
     * @param authentication 当前审批人
     * @return 更新后的审批申请
     */
    @PostMapping("/{id}/review")
    @PreAuthorize("hasAuthority('PERM_APPROVAL_REVIEW')")
    public ApiResponse<ApprovalRequest> review(
            @PathVariable Long id,
            @RequestBody ReviewRequest request,
            Authentication authentication) {
        return ApiResponse.ok(service.review(
                id,
                authentication.getName(),
                request.approved(),
                request.remark()));
    }

    /** 审批申请提交参数。 */
    public record ApprovalSubmitRequest(
            @NotBlank String module,
            @NotBlank String action,
            @NotBlank String payload) {
    }

    /** 审批决定参数。 */
    public record ReviewRequest(boolean approved, String remark) {
    }
}
