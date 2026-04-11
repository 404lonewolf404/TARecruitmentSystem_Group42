<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.UserRole" %>
<%@ page import="com.bupt.tarecruitment.service.MessageService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || currentUser.getRole() != UserRole.TA) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    
    List<MessageService.ConversationInfo> conversations = 
        (List<MessageService.ConversationInfo>) request.getAttribute("conversations");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>消息 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .messages-container {
            max-width: 800px;
            margin: 20px auto;
        }
        
        .conversation-item {
            background: white;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 10px;
            cursor: pointer;
            transition: all 0.3s;
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .conversation-item:hover {
            background: #f8f9fa;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        
        .conversation-avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #3498db;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            font-weight: bold;
            flex-shrink: 0;
        }
        
        .conversation-content {
            flex: 1;
            min-width: 0;
        }
        
        .conversation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 5px;
        }
        
        .conversation-name {
            font-weight: bold;
            font-size: 16px;
            color: #2c3e50;
        }
        
        .conversation-time {
            font-size: 12px;
            color: #999;
        }
        
        .conversation-preview {
            color: #666;
            font-size: 14px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        
        .conversation-badge {
            background: #e74c3c;
            color: white;
            border-radius: 12px;
            padding: 2px 8px;
            font-size: 12px;
            font-weight: bold;
            flex-shrink: 0;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .empty-state-icon {
            font-size: 64px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/ta/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/profile">个人资料</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/positions">浏览职位</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/applications/my">我的申请</a></li>
            <li class="active"><a href="<%= request.getContextPath() %>/messages/list">消息</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/ta/notifications">
                    通知
                    <% 
                        Integer unreadCount = (Integer) request.getAttribute("unreadNotificationCount");
                        if (unreadCount != null && unreadCount > 0) { 
                    %>
                        <span class="notification-badge"><%= unreadCount %></span>
                    <% } %>
                </a>
            </li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <h2>我的消息</h2>
        
        <div class="messages-container">
            <% if (conversations == null || conversations.isEmpty()) { %>
                <div class="empty-state">
                    <div class="empty-state-icon">💬</div>
                    <h3>暂无消息</h3>
                    <p>当您与MO开始对话后，消息将显示在这里</p>
                </div>
            <% } else { %>
                <% for (MessageService.ConversationInfo conv : conversations) { %>
                    <div class="conversation-item" onclick="location.href='<%= request.getContextPath() %>/messages/conversation?applicationId=<%= conv.getApplicationId() %>'">
                        <div class="conversation-avatar">
                            <%= conv.getOtherUser() != null ? conv.getOtherUser().getName().substring(0, 1) : "?" %>
                        </div>
                        <div class="conversation-content">
                            <div class="conversation-header">
                                <div class="conversation-name">
                                    <%= conv.getOtherUser() != null ? conv.getOtherUser().getName() : "未知用户" %>
                                    <span style="color: #999; font-size: 14px; font-weight: normal;">
                                        - <%= conv.getPosition() != null ? conv.getPosition().getTitle() : "未知职位" %>
                                    </span>
                                </div>
                                <div class="conversation-time">
                                    <%= dateFormat.format(conv.getLastMessage().getSentAt()) %>
                                </div>
                            </div>
                            <div class="conversation-preview">
                                <%= conv.getLastMessage().getContent() %>
                            </div>
                        </div>
                        <% if (conv.getUnreadCount() > 0) { %>
                            <div class="conversation-badge"><%= conv.getUnreadCount() %></div>
                        <% } %>
                    </div>
                <% } %>
            <% } %>
        </div>
    </div>
</body>
</html>
