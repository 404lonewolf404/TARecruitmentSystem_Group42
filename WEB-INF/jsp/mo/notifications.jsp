<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Notification" %>
<%@ page import="com.bupt.tarecruitment.model.NotificationType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
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
    <title>Notification Center - TA Recruitment System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <header>
        <h1><i class="fas fa-graduation-cap"></i> TA Recruitment System</h1>
    </header>
    
    <nav>
        <ul>
            <li><a href="<%= request.getContextPath() %>/mo/dashboard"><i class="fas fa-home"></i>&nbsp;&nbsp;Dashboard</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/profile"><i class="fas fa-user"></i>&nbsp;&nbsp;Profile</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/my"><i class="fas fa-briefcase"></i>&nbsp;&nbsp;My Positions</a></li>
            <li><a href="<%= request.getContextPath() %>/mo/positions/create"><i class="fas fa-plus-circle"></i>&nbsp;&nbsp;Create Position</a></li>
            <li><a href="<%= request.getContextPath() %>/messages/list"><i class="fas fa-comments"></i>&nbsp;&nbsp;Messages</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/mo/notifications">
                    <i class="fas fa-bell"></i>&nbsp;&nbsp;Notifications
                    <% if (unreadCount > 0) { %>
                        <span class="notification-badge"><%= unreadCount %></span>
                    <% } %>
                </a>
            </li>
            <li><a href="<%= request.getContextPath() %>/auth/logout"><i class="fas fa-sign-out-alt"></i>&nbsp;&nbsp;Logout</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="page-header">
            <h2><i class="fas fa-bell"></i>&nbsp;&nbsp;Notification Center</h2>
            <p>Track new applications and system updates</p>
        </div>
        
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="error-message" style="margin-bottom: 25px;">
                <i class="fas fa-exclamation-circle"></i>&nbsp;&nbsp;<%= errorMessage %>
            </div>
        <% } %>
        
        <% if (unreadCount > 0) { %>
            <div class="card" style="margin-bottom: 25px;">
                <form action="<%= request.getContextPath() %>/notifications/markAllRead" method="post">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-check-double"></i>&nbsp;&nbsp;Mark All as Read
                    </button>
                </form>
            </div>
        <% } %>
        
        <% if (notifications == null || notifications.isEmpty()) { %>
            <div class="card">
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h3>No Notifications</h3>
                    <p>You're all caught up! No new notifications.</p>
                </div>
            </div>
        <% } else { %>
            <div class="notifications-list">
                <% for (Notification notification : notifications) { 
                    String typeClass = "";
                    String typeIcon = "";
                    String typeName = "";
                    
                    if (notification.getType() == NotificationType.NEW_APPLICATION) {
                        typeClass = "type-new";
                        typeIcon = "fas fa-file-alt";
                        typeName = "New Application";
                    } else if (notification.getType() == NotificationType.APPLICATION_WITHDRAWN) {
                        typeClass = "type-withdrawn";
                        typeIcon = "fas fa-undo";
                        typeName = "Application Withdrawn";
                    } else {
                        typeIcon = "fas fa-info-circle";
                        typeName = notification.getType().toString();
                    }
                %>
                    <div class="card notification-card <%= notification.isRead() ? "read" : "unread" %>">
                        <div class="notification-header-modern">
                            <div class="notification-type-badge <%= typeClass %>">
                                <i class="<%= typeIcon %>"></i>&nbsp;&nbsp;<%= typeName %>
                            </div>
                            <span class="notification-time-modern">
                                <i class="fas fa-clock"></i>&nbsp;&nbsp;<%= dateFormat.format(notification.getCreatedAt()) %>
                            </span>
                        </div>
                        
                        <div class="notification-message-modern">
                            <%= notification.getMessage() %>
                        </div>
                        
                        <div class="notification-actions-modern">
                            <% if (!notification.isRead()) { %>
                                <form action="<%= request.getContextPath() %>/notifications/markRead" method="post" style="display: inline;">
                                    <input type="hidden" name="notificationId" value="<%= notification.getNotificationId() %>">
                                    <button type="submit" class="btn btn-secondary btn-sm">
                                        <i class="fas fa-check"></i>&nbsp;&nbsp;Mark as Read
                                    </button>
                                </form>
                            <% } %>
                            <form action="<%= request.getContextPath() %>/notifications/delete" method="post" style="display: inline;">
                                <input type="hidden" name="notificationId" value="<%= notification.getNotificationId() %>">
                                <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Delete this notification?')">
                                    <i class="fas fa-trash-alt"></i>&nbsp;&nbsp;Delete
                                </button>
                            </form>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } %>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
