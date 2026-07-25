package com.example.mes.approval.application;

/** 审批通过后的业务动作执行器，负责将审批数据转换为具体模块操作。 */
public interface ApprovalActionExecutor {
    /**
     * 判断执行器是否支持指定的审批模块和动作。
     *
     * @param module 业务模块名称
     * @param action 业务动作名称
     * @return 是否支持该审批动作
     */
    boolean supports(String module, String action);

    /**
     * 执行审批通过后的业务变更。
     *
     * @param action 业务动作名称
     * @param payload 审批申请保存的 JSON 载荷
     * @return 无返回值
     * @throws IllegalArgumentException 载荷格式错误或动作参数不合法
     */
    void execute(String action, String payload);
}
