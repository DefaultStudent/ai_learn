package com.example.mes.common.logging;

import com.example.mes.approval.domain.ApprovalRequest;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/** 统一处理审计注解，将关键业务操作写入数据库和 Log4j2。 */
@Aspect
@Component
public class AuditLogAspect {
    private final SystemLogService systemLogService;

    /** @param systemLogService 系统日志服务 */
    public AuditLogAspect(SystemLogService systemLogService) { this.systemLogService = systemLogService; }

    /**
     * @param joinPoint 被审计的方法调用上下文
     * @return 被审计方法的返回值
     * @throws Throwable 被审计方法抛出的异常
     */
    @Around("@annotation(com.example.mes.common.logging.AuditLog)")
    public Object recordAuditLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        AuditLog metadata = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(AuditLog.class);
        Object[] arguments = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        String operator = arguments.length > 1 && "review".equals(methodName) ? String.valueOf(arguments[1]) : arguments.length > 0 ? String.valueOf(arguments[0]) : "system";
        String message = buildMessage(methodName, metadata, arguments, result);
        String action = "review".equals(methodName) && arguments.length > 2 && Boolean.TRUE.equals(arguments[2]) ? "APPROVE" : "review".equals(methodName) ? "REJECT" : metadata.action();
        String level = "REJECT".equals(action) ? "WARN" : metadata.level();
        systemLogService.record(level, metadata.module(), action, message, operator);
        return result;
    }

    /**
     * @param methodName 被调用的方法名
     * @param metadata 审计注解元数据
     * @param arguments 方法参数
     * @param result 方法返回值
     * @return 审计日志内容
     */
    private String buildMessage(String methodName, AuditLog metadata, Object[] arguments, Object result) {
        if ("internalError".equals(methodName) && arguments.length > 0 && arguments[0] instanceof Exception exception) {
            return "REST 请求处理失败: " + exception.getMessage();
        }
        if ("review".equals(methodName)) {
            boolean approved = arguments.length > 2 && Boolean.TRUE.equals(arguments[2]);
            return (approved ? "批准" : "驳回") + "审批申请 #" + arguments[0];
        }
        if (result instanceof ApprovalRequest request) return "提交审批申请 #" + request.getId() + ": " + metadata.module() + "/" + metadata.action();
        return metadata.module() + "/" + metadata.action() + " " + Arrays.toString(arguments);
    }
}
