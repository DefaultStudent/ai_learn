package com.example.mes.security;

import com.example.mes.common.api.ApiResponse;
import com.example.mes.user.api.UserDtos.LoginRequest;
import com.example.mes.user.api.UserDtos.LoginResponse;
import com.example.mes.user.infrastructure.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 登录、当前用户和退出会话 API。 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final PermissionVerificationService permissionVerificationService;
    private final UserRepository repository;

    /** @param authenticationManager Spring 认证管理器 @param repository 用户仓储 */
    public AuthController(PermissionVerificationService permissionVerificationService, UserRepository repository) { this.permissionVerificationService = permissionVerificationService; this.repository = repository; }

    /**
     * 登录并写入服务器会话。
     * @param request 登录账号和密码
     * @param httpRequest HTTP 请求，用于保存认证会话
     * @return 当前登录用户
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        PermissionVerificationService.VerifiedLogin verifiedLogin = permissionVerificationService.verify(request);
        Authentication authentication = verifiedLogin.authentication();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        session.setAttribute("MES_LOGIN_USER", verifiedLogin.user());
        return ApiResponse.ok(verifiedLogin.user());
    }

    /** @param authentication 当前会话认证信息 @return 当前用户 */
    @GetMapping("/me")
    public ApiResponse<LoginResponse> me(Authentication authentication, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        LoginResponse sessionUser = session == null ? null : (LoginResponse) session.getAttribute("MES_LOGIN_USER");
        if (sessionUser != null) return ApiResponse.ok(sessionUser);
        return ApiResponse.ok(LoginResponse.from(repository.findByUsername(authentication.getName()).orElseThrow()));
    }
}
