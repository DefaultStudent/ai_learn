package com.example.mes.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.mes.common.api.ApiResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 为实时通知客户端签发受限的 Centrifugo JWT。 */
@RestController
@RequestMapping("/api/realtime")
public class CentrifugoTokenController {
    private final Algorithm algorithm;

    /** @param secret Centrifugo JWT 签名密钥 */
    public CentrifugoTokenController(@Value("${mes.realtime.centrifugo.token-secret:mes-learning-dev-secret-change-me}") String secret) { this.algorithm = Algorithm.HMAC256(secret); }

    /**
     * @param authentication 当前登录用户
     * @return 仅允许订阅审批频道的 JWT
     */
    @GetMapping("/token")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> token(Authentication authentication) {
        List<String> channels = new ArrayList<>(List.of("approval:requester:" + authentication.getName()));
        boolean canReview = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("PERM_APPROVAL_REVIEW"));
        if (canReview) channels.add("approval:admins");
        String token = JWT.create().withSubject(authentication.getName()).withClaim("channels", channels).withExpiresAt(Instant.now().plusSeconds(3600)).sign(algorithm);
        return ApiResponse.ok(token);
    }
}
