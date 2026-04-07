<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Notification" %>
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
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>通知中心 - TA招聘系统</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <header>
        <h1>TA招聘系统</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/admin/dashboard">仪表板</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/users">用户管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/positions">职位管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/applications">申请管理</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/workload">工作量报告</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/notifications" class="active">通知</a></li>
            <li><a href="<%= request.getContextPath() %>/auth/logout">登出</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <h2>通知中心</h2>
        
        <% if (notifications != null && !notifications.isEmpty()) { %>
            <div class="notification-actions">
                <form action="<%= request.getContextPath() %>/notifications/markAllRead" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-secondary">全部标记为已读</button>
                </form>
            </div>
            
            <div class="notifications-list">
                <% for (Notification notification : notifications) { %>
                    <div class="notification-item <%= notification.isRead() ? "read" : "unread" %>">
                        <div class="notification-content">
                            <div class="notification-message"><%= notification.getMessage() %></div>
                            <div class="notification-time"><%= sdf.format(notification.getCreatedAt()) %></div>
                        </div>
                        <div class="notification-actions">
                            <% if (!notification.isRead()) { %>
                                <form action="<%= request.getContextPath() %>/notifications/markRead" method="post" style="display: inline;">
                                    <input type="hidden" name="notificationId" value="<%= notification.getNotificationId() %>">
                                    <button type="submit" class="btn btn-sm">标记已读</button>
                                </form>
                            <% } %>
                            <form action="<%= request.getContextPath() %>/notifications/delete" method="post" style="display: inline;">
                                <input type="hidden" name="notificationId" value="<%= notification.getNotificationId() %>">
                                <button type="submit" class="btn btn-sm btn-danger">删除</button>
                            </form>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } else { %>
            <div class="card">
                <p>暂无通知</p>
            </div>
        <% } %>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
