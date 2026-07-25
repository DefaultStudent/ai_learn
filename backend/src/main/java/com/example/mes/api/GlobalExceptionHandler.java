package com.example.mes.api;

import com.example.mes.common.api.ApiResponse;
import com.example.mes.common.exception.ResourceNotFoundException;
import com.example.mes.common.logging.AuditLog;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** 全局 REST 异常适配器，保证所有模块返回一致的 ApiResponse 结构。 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    /**
     * 将资源不存在异常转换为 404 响应。
     * @param exception 资源不存在异常，包含前台可展示的资源标识
     * @return 统一 REST 错误响应
     */
    public ApiResponse<Void> notFound(ResourceNotFoundException exception) {
        return new ApiResponse<>(null, exception.getMessage());
    }

    /** 将 @Valid 的字段错误压缩为前台可直接展示的一行消息。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    /**
     * 转换请求体字段校验异常。
     * @param exception Spring MVC 收集的字段校验异常
     * @return 包含字段错误信息的统一 REST 响应
     */
    public ApiResponse<Void> validation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return new ApiResponse<>(null, message);
    }

    @ExceptionHandler({ IllegalArgumentException.class, ConstraintViolationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    /**
     * 转换业务参数异常和约束校验异常。
     * @param exception 参数或约束校验异常
     * @return 400 统一 REST 错误响应
     */
    public ApiResponse<Void> badRequest(Exception exception) {
        return new ApiResponse<>(null, exception.getMessage());
    }

    /**
     * @param exception 权限拒绝异常
     * @return 统一的 403 错误响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> forbidden(AccessDeniedException exception) { return new ApiResponse<>(null, "没有访问该资源的权限"); }

    /** 未预期异常只返回安全提示，详细堆栈写入服务端日志。 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @AuditLog(module = "api", action = "REST_ERROR", level = "ERROR")
    /**
     * 兜底处理未预期异常。
     * @param exception 未预期的服务端异常
     * @return 不暴露内部堆栈的 500 错误响应
     */
    public ApiResponse<Void> internalError(Exception exception) {
        return new ApiResponse<>(null, "服务器内部错误");
    }
}
