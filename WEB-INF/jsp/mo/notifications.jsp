<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Notification" %>
<%@ page import="com.bupt.tarecruitment.model.NotificationType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    @SuppressWarnings("unchecked")
    List<Notification> notifications = (List<Notification>) request.getAttribute("notifications");
    Integer unreadCount = (Integer) request.getAttribute("unreadCount");
    String errorMessage = (String) request.getAttribute("errorMessage");
    
    if (unreadCount == null) unreadCount = 0;
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>通知中心 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .notification-item {
            background: white;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 20px;
            margin-bottom: 15px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .notification-item.unread {
            background: #ecf0f1;
            border-left: 4px solid #f39c12;
        }
        
        .notification-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 1px solid #ecf0f1;
        }
        
        .notification-type {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 4px;
            font-size: 13px;
            font-weight: 600;
        }
        
        .type-new {
            background: #27ae60;
            color: white;
        }
        
        .type-withdrawn {
            background: #e67e22;
            color: white;
        }
        
        .notification-time {
            color: #7f8c8d;
            font-size: 14px;
        }
        
        .notification-message {
            color: #2c3e50;
            line-height: 1.8;
            white-space: pre-line;
            font-size: 15px;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .notification-actions {
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #ecf0f1;
            display: flex;
            gap: 10px;
        }
        
        .btn-mark-read {
            padding: 8px 16px;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s;
        }
        
        .btn-mark-read:hover {
            background: #2980b9;
        }
        
        .btn-delete {
            padding: 8px 16px;
            background: #e74c3c;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            transition: background-color 0.3s;
        }
        
        .btn-delete:hover {
            background: #c0392b;
        }
        
        .mark-all-read-btn {
            margin-bottom: 20px;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #95a5a6;
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
            <li><a href="<%= request.getContextPath() %>/mo/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/profile">个人资料</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/my">我的职位</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/create">创建职位</a></li>
            <li><a href="<%= request.getContextPath() %>/messages/list">💬 消息</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/mo/notifications">
                    通知
                    <% if (unreadCount > 0) { %>
                        <span class="notification-badge"><%= unreadCount %></span>
                    <% } %>
                </a>
            </li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <h2>📬 通知中心</h2>
        
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="error-message"><%= errorMessage %></div>
        <% } %>
        
        <% if (unreadCount > 0) { %>
            <form action="<%= request.getContextPath() %>/notifications/markAllRead" method="post" class="mark-all-read-btn">
                <button type="submit" class="btn btn-secondary">全部标记为已读</button>
            </form>
        <% } %>
        
        <% if (notifications == null || notifications.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-state-icon">🔔</div>
                <p>暂无通知</p>
            </div>
        <% } else { %>
            <% for (Notification notification : notifications) { 
                String typeClass = "";
                String typeName = "";
                
                if (notification.getType() == NotificationType.NEW_APPLICATION) {
                    typeClass = "type-new";
                    typeName = "新申请";
                } else if (notification.getType() == NotificationType.APPLICATION_WITHDRAWN) {
                    typeClass = "type-withdrawn";
                    typeName = "申请撤回";
                } else {
                    typeName = notification.getType().toString();
                }
            %>
                <div class="notification-item <%= notification.isRead() ? "" : "unread" %>">
                    <div class="notification-header">
                        <span class="notification-type <%= typeClass %>">
                            <%= typeName %>
                        </span>
                        <span class="notification-time">
                            <%= dateFormat.format(notification.getCreatedAt()) %>
                        </span>
                    </div>
                    
                    <div class="notification-message"><%= notification.getMessage() %></div>
                    
                    <div class="notification-actions">
                        <% if (!notification.isRead()) { %>
                            <form action="<%= request.getContextPath() %>/notifications/markRead" method="post" style="display: inline;">
                                <input type="hidden" name="notificationId" value="<%= notification.getNotificationId() %>">
                                <button type="submit" class="btn-mark-read">标记为已读</button>
                            </form>
                        <% } %>
                        <form action="<%= request.getContextPath() %>/notifications/delete" method="post" style="display: inline;">
                            <input type="hidden" name="notificationId" value="<%= notification.getNotificationId() %>">
                            <button type="submit" class="btn-delete" onclick="return confirm('确定要删除这条通知吗？')">删除</button>
                        </form>
                    </div>
                </div>
            <% } %>
        <% } %>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
