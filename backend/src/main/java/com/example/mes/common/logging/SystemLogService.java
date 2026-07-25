package com.example.mes.common.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** 系统日志服务：同时输出 Log4j2 日志并持久化关键业务日志。 */
@Service
public class SystemLogService {
    private static final Logger log = LogManager.getLogger(SystemLogService.class);
    private final SystemLogRepository repository;

    /**
     * @param repository 系统日志仓储
     */
    public SystemLogService(SystemLogRepository repository) {
        this.repository = repository;
    }

    /**
     * 记录一条关键操作日志。日志使用独立事务，避免业务事务回滚时丢失审计记录。
     * @param level 日志级别
     * @param module 业务模块
     * @param action 业务动作
     * @param message 业务日志内容
     * @param operatorName 操作人
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String level, String module, String action, String message, String operatorName) {
        writeToLog4j(level, module, action, message);
        repository.save(new SystemLog(level, module, action, message, operatorName));
    }

    /**
     * 记录 INFO 级别关键日志。
     * @param module 业务模块
     * @param action 业务动作
     * @param message 业务日志内容
     */
    public void info(String module, String action, String message) {
        record("INFO", module, action, message, "system");
    }

    /**
     * 记录 WARN 级别关键日志。
     * @param module 业务模块
     * @param action 业务动作
     * @param message 业务日志内容
     */
    public void warn(String module, String action, String message) {
        record("WARN", module, action, message, "system");
    }

    /**
     * 记录 ERROR 级别关键日志。
     * @param module 业务模块
     * @param action 业务动作
     * @param message 业务日志内容
     */
    public void error(String module, String action, String message) {
        record("ERROR", module, action, message, "system");
    }

    /**
     * 输出到 Log4j2，级别未知时降级为 INFO。
     * @param level 日志级别
     * @param module 业务模块
     * @param action 业务动作
     * @param message 业务日志内容
     */
    private void writeToLog4j(String level, String module, String action, String message) {
        String formatted = "[" + module + "][" + action + "] " + message;
        switch (level.toUpperCase()) {
            case "ERROR" -> log.error(formatted);
            case "WARN" -> log.warn(formatted);
            default -> log.info(formatted);
        }
    }
}
