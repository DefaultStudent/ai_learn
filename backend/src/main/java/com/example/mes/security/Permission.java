package com.example.mes.security;

/** 后端统一定义的业务权限，权限名称是系统授权的唯一事实来源。 */
public enum Permission {
    USER_MANAGE,
    SYSTEM_LOG_VIEW,
    DEVICE_VIEW,
    DEVICE_MANAGE,
    DEVICE_PROPOSE,
    PRODUCTION_VIEW,
    APPROVAL_SUBMIT,
    APPROVAL_MINE,
    APPROVAL_REVIEW
}
