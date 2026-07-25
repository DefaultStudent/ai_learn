package com.example.mes.user.infrastructure;

import com.example.mes.user.domain.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** 用户 JPA 持久化接口。 */
public interface UserRepository extends JpaRepository<AppUser, Long> {
    /** @param username 登录账号 @return 用户查询结果 */
    Optional<AppUser> findByUsername(String username);
    /** @param username 登录账号 @return 账号是否存在 */
    boolean existsByUsername(String username);
}
