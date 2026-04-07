package com.bupt.tarecruitment.model;

import java.util.Date;

/**
 * 消息模型
 * V3.5 - 招聘对话系统
 */
public class Message {
    private String messageId;
    private String applicationId;
    private String senderId;
    private UserRole senderRole;
    private String content;
    private Date sentAt;
    private boolean isRead;
    
    // 构造函数
    public Message() {
    }
    
    public Message(String messageId, String applicationId, String senderId, 
                   UserRole senderRole, String content, Date sentAt, boolean isRead) {
        this.messageId = messageId;
        this.applicationId = applicationId;
        this.senderId = senderId;
        this.senderRole = senderRole;
        this.content = content;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }
    
    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public UserRole getSenderRole() {
        return senderRole;
    }
    
    public void setSenderRole(UserRole senderRole) {
        this.senderRole = senderRole;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Date getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
}
