package com.example.mes.common.logging;

import org.springframework.data.jpa.repository.JpaRepository;

/** 系统日志持久化接口。 */
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {}
