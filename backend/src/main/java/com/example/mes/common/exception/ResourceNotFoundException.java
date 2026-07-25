package com.example.mes.common.exception;

/** 资源不存在的跨模块基础异常，由全局 API 层转换为 HTTP 404。 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * @param message 面向调用方的资源不存在描述
     */
    public ResourceNotFoundException(String message) { super(message); }
}
