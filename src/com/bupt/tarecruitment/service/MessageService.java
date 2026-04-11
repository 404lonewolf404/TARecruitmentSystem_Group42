package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.MessageDAO;
import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Message;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.User;
import com.bupt.tarecruitment.model.UserRole;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 消息服务
 * V3.5 - 招聘对话系统
 */
public class MessageService {
    
    private MessageDAO messageDAO;
    private ApplicationDAO applicationDAO;
    private UserDAO userDAO;
    
    public MessageService() {
        this.messageDAO = new MessageDAO();
        this.applicationDAO = new ApplicationDAO();
        this.userDAO = new UserDAO();
    }
    
    /**
     * 发送消息
     */
    public void sendMessage(String applicationId, String senderId, String content) throws IOException {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        
        // 验证申请是否存在
        Application application = applicationDAO.findById(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("申请不存在");
        }
        
        // 获取发送者信息
        User sender = userDAO.findById(senderId);
        if (sender == null) {
            throw new IllegalArgumentException("发送者不存在");
        }
        
        // 验证发送者是否有权限（必须是申请的TA或职位的MO）
        if (!application.getTaId().equals(senderId)) {
            // 如果不是TA，检查是否是MO
            com.bupt.tarecruitment.dao.PositionDAO positionDAO = new com.bupt.tarecruitment.dao.PositionDAO();
            com.bupt.tarecruitment.model.Position position = positionDAO.findById(application.getPositionId());
            if (position == null || !position.getMoId().equals(senderId)) {
                throw new IllegalArgumentException("您没有权限发送此消息");
            }
        }
        
        // 创建消息
        Message message = new Message();
        message.setMessageId(UUID.randomUUID().toString());
        message.setApplicationId(applicationId);
        message.setSenderId(senderId);
        message.setSenderRole(sender.getRole());
        message.setContent(content.trim());
        message.setSentAt(new Date());
        message.setRead(false);
        
        messageDAO.add(message);
    }
    
    /**
     * 获取对话历史
     */
    public List<Message> getConversation(String applicationId) {
        return messageDAO.findByApplicationId(applicationId);
    }
    
    /**
     * 获取未读消息数量
     */
    public int getUnreadCount(String userId, String applicationId) {
        return messageDAO.getUnreadCount(userId, applicationId);
    }
    
    /**
     * 标记所有消息为已读
     */
    public void markAllAsRead(String applicationId, String userId) throws IOException {
        messageDAO.markAllAsRead(applicationId, userId);
    }
    
    /**
     * 获取申请的未读消息数量（用于显示徽章）
     */
    public int getUnreadCountForApplication(String userId, String applicationId) {
        return messageDAO.getUnreadCount(userId, applicationId);
    }
    
    /**
     * 获取用户的所有对话列表
     * V3.5 Part 2 - 消息列表功能
     */
    public java.util.List<ConversationInfo> getUserConversations(String userId) {
        java.util.List<ConversationInfo> conversations = new java.util.ArrayList<>();
        
        // 获取用户的所有申请
        java.util.Set<String> applicationIds = new java.util.HashSet<>();
        
        // 如果是TA，获取自己的申请
        List<Application> userApplications = applicationDAO.findByTaId(userId);
        for (Application app : userApplications) {
            applicationIds.add(app.getApplicationId());
        }
        
        // 如果是MO，获取自己职位的申请
        com.bupt.tarecruitment.dao.PositionDAO positionDAO = new com.bupt.tarecruitment.dao.PositionDAO();
        List<com.bupt.tarecruitment.model.Position> userPositions = positionDAO.findByMoId(userId);
        for (com.bupt.tarecruitment.model.Position position : userPositions) {
            List<Application> positionApps = applicationDAO.findByPositionId(position.getPositionId());
            for (Application app : positionApps) {
                applicationIds.add(app.getApplicationId());
            }
        }
        
        // 对每个申请，获取对话信息
        for (String appId : applicationIds) {
            List<Message> messages = messageDAO.findByApplicationId(appId);
            if (!messages.isEmpty()) {
                Application app = applicationDAO.findById(appId);
                if (app != null) {
                    com.bupt.tarecruitment.model.Position position = positionDAO.findById(app.getPositionId());
                    User ta = userDAO.findById(app.getTaId());
                    User mo = position != null ? userDAO.findById(position.getMoId()) : null;
                    
                    // 获取最后一条消息
                    Message lastMessage = messages.get(messages.size() - 1);
                    
                    // 获取未读消息数
                    int unreadCount = messageDAO.getUnreadCount(userId, appId);
                    
                    // 确定对方用户
                    User otherUser = userId.equals(app.getTaId()) ? mo : ta;
                    
                    ConversationInfo info = new ConversationInfo();
                    info.setApplicationId(appId);
                    info.setApplication(app);
                    info.setPosition(position);
                    info.setTa(ta);
                    info.setMo(mo);
                    info.setOtherUser(otherUser);
                    info.setLastMessage(lastMessage);
                    info.setUnreadCount(unreadCount);
                    
                    conversations.add(info);
                }
            }
        }
        
        // 按最后消息时间排序
        conversations.sort((c1, c2) -> c2.getLastMessage().getSentAt().compareTo(c1.getLastMessage().getSentAt()));
        
        return conversations;
    }
    
    /**
     * 对话信息类
     */
    public static class ConversationInfo {
        private String applicationId;
        private Application application;
        private com.bupt.tarecruitment.model.Position position;
        private User ta;
        private User mo;
        private User otherUser;
        private Message lastMessage;
        private int unreadCount;
        
        public String getApplicationId() { return applicationId; }
        public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
        
        public Application getApplication() { return application; }
        public void setApplication(Application application) { this.application = application; }
        
        public com.bupt.tarecruitment.model.Position getPosition() { return position; }
        public void setPosition(com.bupt.tarecruitment.model.Position position) { this.position = position; }
        
        public User getTa() { return ta; }
        public void setTa(User ta) { this.ta = ta; }
        
        public User getMo() { return mo; }
        public void setMo(User mo) { this.mo = mo; }
        
        public User getOtherUser() { return otherUser; }
        public void setOtherUser(User otherUser) { this.otherUser = otherUser; }
        
        public Message getLastMessage() { return lastMessage; }
        public void setLastMessage(Message lastMessage) { this.lastMessage = lastMessage; }
        
        public int getUnreadCount() { return unreadCount; }
        public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    }
}
