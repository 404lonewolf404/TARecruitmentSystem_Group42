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
}
