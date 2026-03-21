package com.bupt.tarecruitment.model;

/**
 * 申请状态枚�?
 * 定义申请的四种状�?
 */
public enum ApplicationStatus {
    /**
     * 待审�?- 申请已提交，等待MO审核
     */
    PENDING,
    
    /**
     * 已选中 - MO已选择该申请�?
     */
    SELECTED,
    
    /**
     * 已拒�?- MO已拒绝该申请
     */
    REJECTED,
    
    /**
     * 已撤�?- TA已撤回该申请
     */
    WITHDRAWN
}
