<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    
    // Get unread notification count
    int unreadCount = 0;
    try {
        NotificationService notificationService = new NotificationService();
        unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile - TA Recruitment System</title>
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
            <h2><i class="fas fa-user-circle"></i>&nbsp;&nbsp;My Profile</h2>
            <p>Update your personal information</p>
        </div>
        
        <div class="card" style="max-width: 600px; margin: 0 auto;">
            <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
            <% String successMessage = (String) request.getAttribute("successMessage"); %>
            
            <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i>&nbsp;&nbsp;<%= errorMessage %>
                </div>
            <% } %>
            
            <% if (successMessage != null && !successMessage.isEmpty()) { %>
                <div class="success-message">
                    <i class="fas fa-check-circle"></i>&nbsp;&nbsp;<%= successMessage %>
                </div>
            <% } %>
            
            <form action="<%= request.getContextPath() %>/mo/profile" method="post" class="profile-form">
                <div class="form-group">
                    <label for="name"><i class="fas fa-user"></i>&nbsp;&nbsp;Full Name</label>
                    <input type="text" id="name" name="name" value="<%= currentUser.getName() %>" required>
                </div>
                
                <div class="form-group">
                    <label for="email"><i class="fas fa-envelope"></i>&nbsp;&nbsp;Email Address</label>
                    <input type="email" id="email" name="email" value="<%= currentUser.getEmail() %>" required>
                </div>
                
                <div class="form-group">
                    <label><i class="fas fa-id-badge"></i>&nbsp;&nbsp;Role</label>
                    <input type="text" value="Module Owner (MO)" readonly disabled style="background-color: #f8fafc; cursor: not-allowed;">
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-full">
                        <i class="fas fa-save"></i>&nbsp;&nbsp;Save Changes
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
