package com.example.mes.user.config;

import com.example.mes.user.domain.AppUser;
import com.example.mes.user.domain.UserRole;
import com.example.mes.user.infrastructure.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** 学习环境默认账号初始化；生产环境必须改为安全的外部初始化流程。 */
@Configuration
public class DefaultUserData {
    /**
     * @param repository 用户仓储
     * @param encoder 密码编码器
     * @return 默认账号初始化任务
     */
    @Bean
    CommandLineRunner seedUsers(UserRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (!repository.existsByUsername("sysadmin")) repository.save(new AppUser("sysadmin", encoder.encode("sysadmin123"), "系统管理员", UserRole.SYSTEM_ADMIN, "系统"));
            if (!repository.existsByUsername("admin")) repository.save(new AppUser("admin", encoder.encode("admin123"), "管理员", UserRole.ADMIN, "系统"));
            if (!repository.existsByUsername("device_manager")) repository.save(new AppUser("device_manager", encoder.encode("device123"), "设备部门管理者", UserRole.DEVICE_MANAGER, "设备部"));
            if (!repository.existsByUsername("device_user")) repository.save(new AppUser("device_user", encoder.encode("device123"), "设备部门用户", UserRole.DEVICE_USER, "设备部"));
            if (!repository.existsByUsername("production_manager")) repository.save(new AppUser("production_manager", encoder.encode("production123"), "生产部门管理者", UserRole.PRODUCTION_MANAGER, "生产部"));
            if (!repository.existsByUsername("production_user")) repository.save(new AppUser("production_user", encoder.encode("production123"), "生产部门用户", UserRole.PRODUCTION_USER, "生产部"));
        };
    }
}
