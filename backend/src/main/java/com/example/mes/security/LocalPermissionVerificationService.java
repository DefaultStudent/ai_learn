package com.example.mes.security;

import com.example.mes.user.api.UserDtos.LoginRequest;
import com.example.mes.user.api.UserDtos.LoginResponse;
import com.example.mes.user.infrastructure.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/** 默认的本地权限验证实现，便于学习环境直接运行。 */
@Service
@ConditionalOnProperty(name = "mes.auth.provider", havingValue = "local", matchIfMissing = true)
public class LocalPermissionVerificationService implements PermissionVerificationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;

    /** @param authenticationManager Spring Security 认证管理器 @param repository 本地用户仓库 */
    public LocalPermissionVerificationService(AuthenticationManager authenticationManager, UserRepository repository) { this.authenticationManager = authenticationManager; this.repository = repository; }

    /** @param request 登录账号和密码 @return 本地验证结果 */
    @Override
    public VerifiedLogin verify(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return new VerifiedLogin(authentication, LoginResponse.from(repository.findByUsername(request.username()).orElseThrow()));
    }
}
