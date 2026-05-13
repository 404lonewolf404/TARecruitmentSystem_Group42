<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Notification" %>
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
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
            <li><a href="<%= request.getContextPath() %>/admin/dashboard"><i class="fas fa-home"></i>&nbsp;&nbsp;Dashboard</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/profile"><i class="fas fa-user"></i>&nbsp;&nbsp;Profile</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/users"><i class="fas fa-users"></i>&nbsp;&nbsp;User Management</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/positions"><i class="fas fa-briefcase"></i>&nbsp;&nbsp;Position Management</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/applications"><i class="fas fa-file-alt"></i>&nbsp;&nbsp;Application Management</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/workload"><i class="fas fa-chart-bar"></i>&nbsp;&nbsp;Workload Report</a></li>
            <li><a href="<%= request.getContextPath() %>/admin/notifications"><i class="fas fa-bell"></i>&nbsp;&nbsp;Notifications</a></li>
            <li><a href="<%= request.getContextPath() %>/auth/logout"><i class="fas fa-sign-out-alt"></i>&nbsp;&nbsp;Logout</a></li>
        </ul>
    </nav>
    
    <div class="container">
        <div class="page-header">
            <h2><i class="fas fa-bell"></i>&nbsp;&nbsp;Notification Center</h2>
            <p>System notifications and activity updates</p>
        </div>
        
        <% if (notifications != null && !notifications.isEmpty()) { %>
            <div class="card" style="margin-bottom: 25px;">
                <form action="<%= request.getContextPath() %>/notifications/markAllRead" method="post">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-check-double"></i>&nbsp;&nbsp;Mark All as Read
                    </button>
                </form>
            </div>
            
            <div class="notifications-list">
                <% for (Notification notification : notifications) { %>
                    <div class="card notification-card <%= notification.isRead() ? "read" : "unread" %>">
                        <div class="notification-header-modern">
                            <div class="notification-type-badge">
                                <i class="fas fa-info-circle"></i>&nbsp;&nbsp;System Notification
                            </div>
                            <span class="notification-time-modern">
                                <i class="fas fa-clock"></i>&nbsp;&nbsp;<%= sdf.format(notification.getCreatedAt()) %>
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
        <% } else { %>
            <div class="card">
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h3>No Notifications</h3>
                    <p>You're all caught up! No new notifications.</p>
                </div>
            </div>
        <% } %>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
