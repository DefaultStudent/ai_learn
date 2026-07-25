package com.example.mes.user.application;

import com.example.mes.common.exception.ResourceNotFoundException;
import com.example.mes.security.RolePermissionMapper;
import com.example.mes.user.api.UserDtos.LoginResponse;
import com.example.mes.user.api.UserDtos.UserRequest;
import com.example.mes.user.domain.AppUser;
import com.example.mes.user.infrastructure.UserRepository;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 用户查询、认证和用户管理应用服务。 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建用户应用服务。
     *
     * @param repository 用户仓库
     * @param passwordEncoder BCrypt 密码编码器
     */
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 根据登录账号加载 Spring Security 用户。
     *
     * @param username 登录账号
     * @return Spring Security 用户详情
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                user.isEnabled(),
                true,
                true,
                true,
                RolePermissionMapper.authoritiesFor(user.getRole()));
    }

    /**
     * 查询全部用户。
     *
     * @return 用户响应列表
     */
    @Transactional(readOnly = true)
    public List<LoginResponse> list() {
        return repository.findAll().stream().map(LoginResponse::from).toList();
    }

    /**
     * 查询单个用户。
     *
     * @param id 用户主键
     * @return 用户响应
     * @throws ResourceNotFoundException 用户不存在
     */
    @Transactional(readOnly = true)
    public LoginResponse get(Long id) {
        return LoginResponse.from(find(id));
    }

    /**
     * 创建用户并编码密码。
     *
     * @param request 用户创建请求
     * @return 创建后的用户
     * @throws IllegalArgumentException 用户名或密码不符合要求
     */
    @Transactional
    public LoginResponse create(UserRequest request) {
        if (repository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("新用户必须设置密码");
        }
        String passwordHash = passwordEncoder.encode(request.password());
        AppUser user = new AppUser(
                request.username(),
                passwordHash,
                request.displayName(),
                request.role(),
                request.department());
        return LoginResponse.from(repository.save(user));
    }

    /**
     * 更新用户资料和权限。
     *
     * @param id 用户主键
     * @param request 用户更新请求
     * @return 更新后的用户
     * @throws ResourceNotFoundException 用户不存在
     * @throws IllegalArgumentException 用户名与其他用户冲突
     */
    @Transactional
    public LoginResponse update(Long id, UserRequest request) {
        AppUser user = find(id);
        if (!user.getUsername().equals(request.username())
                && repository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        String passwordHash = request.password() == null || request.password().isBlank()
                ? null
                : passwordEncoder.encode(request.password());
        user.update(passwordHash, request.displayName(), request.role(), request.department());
        return LoginResponse.from(user);
    }

    /**
     * 删除用户。
     *
     * @param id 用户主键
     * @return 无返回值
     * @throws ResourceNotFoundException 用户不存在
     */
    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    /**
     * 查询用户实体。
     *
     * @param id 用户主键
     * @return 用户实体
     * @throws ResourceNotFoundException 用户不存在
     */
    private AppUser find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + id));
    }
}
