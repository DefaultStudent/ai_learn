package com.example.mes.security;

import com.example.mes.user.api.UserDtos.LoginRequest;
import com.example.mes.user.api.UserDtos.LoginResponse;
import com.example.mes.user.domain.UserRole;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

/** 独立权限系统适配器，只有将 mes.auth.provider=external 时启用。 */
@Service
@ConditionalOnProperty(name = "mes.auth.provider", havingValue = "external")
public class RemotePermissionVerificationService implements PermissionVerificationService {
    private final RestClient client;
    private final String loginPath;

    /** @param builder Spring HTTP 客户端构建器 */
    public RemotePermissionVerificationService(RestClient.Builder builder, @Value("${mes.auth.external.base-url:http://localhost:8090}") String baseUrl, @Value("${mes.auth.external.login-path:/api/permissions/login}") String loginPath) { this.client = builder.baseUrl(baseUrl).build(); this.loginPath = loginPath; }

    /** @param request 登录账号和凭证 @return 独立权限系统验证结果 */
    @Override
    public VerifiedLogin verify(LoginRequest request) {
        RemoteLoginResponse response = client.post().uri(loginPath).body(request).retrieve().body(RemoteLoginResponse.class);
        if (response == null) throw new IllegalArgumentException("权限验证服务未返回用户信息");
        UserRole role = UserRole.valueOf(response.role());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                response.username(),
                null,
                RolePermissionMapper.authoritiesFor(role));
        return new VerifiedLogin(authentication, LoginResponse.of(
                response.id(),
                response.username(),
                response.displayName(),
                role,
                response.department(),
                response.permissions()));
    }

    /** @param id 用户主键 @param username 登录账号 @param displayName 显示名称 @param role 角色 @param department 部门 */
    private record RemoteLoginResponse(
            Long id,
            String username,
            String displayName,
            String role,
            String department,
            List<String> permissions) {
    }
}
