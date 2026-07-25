package com.example.mes.security;

import com.example.mes.user.api.UserDtos.LoginRequest;
import com.example.mes.user.api.UserDtos.LoginResponse;
import org.springframework.security.core.Authentication;

/** 用户登录权限验证服务抽象，可由本地或独立权限系统实现。 */
public interface PermissionVerificationService {
    /**
     * @param request 登录账号和凭证
     * @return 验证通过后的认证信息和用户信息
     */
    VerifiedLogin verify(LoginRequest request);

    /** @param authentication Spring Security 认证信息 @param user 登录用户信息 */
    record VerifiedLogin(Authentication authentication, LoginResponse user) {
    }
}
