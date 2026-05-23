package com.bupt.tarecruitment.service;

import com.bupt.tarecruitment.dao.ApplicationDAO;
import com.bupt.tarecruitment.dao.MessageDAO;
import com.bupt.tarecruitment.dao.PositionDAO;
import com.bupt.tarecruitment.dao.UserDAO;
import com.bupt.tarecruitment.model.Application;
import com.bupt.tarecruitment.model.Message;
import com.bupt.tarecruitment.model.Position;
import com.bupt.tarecruitment.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service for application conversation messages.
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
     * Sends a new message in an application conversation.
     */
    public void sendMessage(String applicationId, String senderId, String content) throws IOException {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        // 中文说明：确认消息所属申请存在。
        Application application = applicationDAO.findById(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }

        // 中文说明：确认发送者存在。
        User sender = userDAO.findById(senderId);
        if (sender == null) {
            throw new IllegalArgumentException("Sender not found");
        }

        // 中文说明：只有该申请对应的 TA 或岗位所属 MO 可以发送消息。
        if (!application.getTaId().equals(senderId)) {
            PositionDAO positionDAO = new PositionDAO();
            Position position = positionDAO.findById(application.getPositionId());
            if (position == null || !position.getMoId().equals(senderId)) {
                throw new IllegalArgumentException("You do not have permission to send this message");
            }
        }

        // 中文说明：组装并保存消息记录。
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
     * Returns all messages for one application conversation.
     */
    public List<Message> getConversation(String applicationId) {
        return messageDAO.findByApplicationId(applicationId);
    }

    /**
     * Returns unread message count for one conversation.
     */
    public int getUnreadCount(String userId, String applicationId) {
        return messageDAO.getUnreadCount(userId, applicationId);
    }

    /**
     * Marks all messages as read for one conversation.
     */
    public void markAllAsRead(String applicationId, String userId) throws IOException {
        messageDAO.markAllAsRead(applicationId, userId);
    }

    /**
     * Returns unread message count for one application.
     */
    public int getUnreadCountForApplication(String userId, String applicationId) {
        return messageDAO.getUnreadCount(userId, applicationId);
    }

    /**
     * Returns all conversation summaries visible to the user.
     */
    public List<ConversationInfo> getUserConversations(String userId) {
        List<ConversationInfo> conversations = new ArrayList<>();

        // 中文说明：收集该用户能访问的全部申请会话 ID。
        Set<String> applicationIds = new HashSet<>();

        // 中文说明：TA 视角下，加入自己提交过的申请。
        List<Application> userApplications = applicationDAO.findByTaId(userId);
        for (Application app : userApplications) {
            applicationIds.add(app.getApplicationId());
        }

        // 中文说明：MO 视角下，加入自己岗位上的全部申请。
        PositionDAO positionDAO = new PositionDAO();
        List<Position> userPositions = positionDAO.findByMoId(userId);
        for (Position position : userPositions) {
            List<Application> positionApps = applicationDAO.findByPositionId(position.getPositionId());
            for (Application app : positionApps) {
                applicationIds.add(app.getApplicationId());
            }
        }

        // 中文说明：为每个会话构建列表页所需的摘要信息。
        for (String appId : applicationIds) {
            List<Message> messages = messageDAO.findByApplicationId(appId);
            if (!messages.isEmpty()) {
                Application app = applicationDAO.findById(appId);
                if (app != null) {
                    Position position = positionDAO.findById(app.getPositionId());
                    User ta = userDAO.findById(app.getTaId());
                    User mo = position != null ? userDAO.findById(position.getMoId()) : null;

                    // 中文说明：消息列表已按时间排序，最后一条即最近消息。
                    Message lastMessage = messages.get(messages.size() - 1);

                    // 中文说明：统计当前用户在该会话中的未读消息数。
                    int unreadCount = messageDAO.getUnreadCount(userId, appId);

                    // 中文说明：找出会话对端用户，方便列表展示。
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

        // 中文说明：按最近消息时间倒序展示会话。
        conversations.sort((c1, c2) -> c2.getLastMessage().getSentAt().compareTo(c1.getLastMessage().getSentAt()));

        return conversations;
    }

    /**
     * Conversation summary model for the message list page.
     */
    public static class ConversationInfo {
        private String applicationId;
        private Application application;
        private Position position;
        private User ta;
        private User mo;
        private User otherUser;
        private Message lastMessage;
        private int unreadCount;

        public String getApplicationId() { return applicationId; }
        public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

        public Application getApplication() { return application; }
        public void setApplication(Application application) { this.application = application; }

        public Position getPosition() { return position; }
        public void setPosition(Position position) { this.position = position; }

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
