package com.example.mes.user.api;

import com.example.mes.common.api.ApiResponse;
import com.example.mes.user.api.UserDtos.UserRequest;
import com.example.mes.user.application.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** 用户管理 API，仅系统管理员和管理员可访问；前台页面入口仍只向系统管理员展示。 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAuthority('PERM_USER_MANAGE')")
public class UserController {
    private final UserService service;
    /** @param service 用户应用服务 */
    public UserController(UserService service) { this.service = service; }
    /** @return 用户列表 */
    @GetMapping public ApiResponse<List<UserDtos.LoginResponse>> list() { return ApiResponse.ok(service.list()); }
    /** @param id 用户主键 @return 用户详情 */
    @GetMapping("/{id}") public ApiResponse<UserDtos.LoginResponse> get(@PathVariable Long id) { return ApiResponse.ok(service.get(id)); }
    /** @param request 新用户信息 @return 新用户 */
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('PERM_USER_MANAGE')")
    public ApiResponse<UserDtos.LoginResponse> create(@Valid @RequestBody UserRequest request) { return ApiResponse.ok(service.create(request)); }
    /** @param id 用户主键 @param request 更新信息 @return 更新后的用户 */
    @PutMapping("/{id}") @PreAuthorize("hasAuthority('PERM_USER_MANAGE')")
    public ApiResponse<UserDtos.LoginResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) { return ApiResponse.ok(service.update(id, request)); }
    /** @param id 用户主键 */
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasAuthority('PERM_USER_MANAGE')")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
