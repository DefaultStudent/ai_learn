package com.example.mes.common.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 标记需要写入系统审计日志的应用服务方法。 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    /** @return 日志模块 */
    String module();

    /** @return 日志动作 */
    String action();

    /** @return 日志级别 */
    String level() default "INFO";
}
