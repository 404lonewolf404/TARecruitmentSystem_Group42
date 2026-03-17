package com.bupt.tarecruitment.model;

/**
 * 申请状态枚举
 * 定义申请的四种状态
 */
public enum ApplicationStatus {
    /**
     * 待审核 - 申请已提交，等待MO审核
     */
    PENDING,
    
    /**
     * 已选中 - MO已选择该申请者
     */
    SELECTED,
    
    /**
     * 已拒绝 - MO已拒绝该申请
     */
    REJECTED,
    
    /**
     * 已撤回 - TA已撤回该申请
     */
    WITHDRAWN
}
