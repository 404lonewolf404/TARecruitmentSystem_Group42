package com.bupt.tarecruitment.model;

import java.util.Date;

/**
 * 通知模型类
 */
public class Notification {
    private String notificationId;
    private String userId;
    private NotificationType type;
    private String message;
    private String relatedId;
    private boolean isRead;
    private Date createdAt;
    
    public Notification() {
        this.createdAt = new Date();
        this.isRead = false;
    }
    
    public Notification(String notificationId, String userId, NotificationType type, 
                       String message, String relatedId, boolean isRead, Date createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.relatedId = relatedId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public String getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getRelatedId() {
        return relatedId;
    }
    
    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
