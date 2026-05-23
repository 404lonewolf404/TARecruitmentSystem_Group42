package com.bupt.tarecruitment.model;

/**
 * Notification categories used by the system.
 */
public enum NotificationType {
    APPLICATION_SELECTED,    // 中文说明：申请已被录用。
    APPLICATION_REJECTED,    // 中文说明：申请未被录用。
    NEW_APPLICATION,         // 中文说明：收到新的申请。
    APPLICATION_WITHDRAWN,   // 中文说明：申请人撤回申请。
    POSITION_DELETED,        // 中文说明：岗位已删除。
    POSITION_CLOSED,         // 中文说明：岗位已关闭。
    MESSAGE                  // 中文说明：收到新消息。
}
