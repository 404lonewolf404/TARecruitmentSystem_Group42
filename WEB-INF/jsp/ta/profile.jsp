<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%
    User user = (User) request.getAttribute("user");
    if (user == null) {
        user = (User) session.getAttribute("user");
    }
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    
    // Get unread notification count
    int unreadCount = 0;
    try {
        NotificationService notificationService = new NotificationService();
        unreadCount = notificationService.getUnreadCount(user.getUserId());
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");
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
            <li><a href="<%= request.getContextPath() %>/ta/dashboard"><i class="fas fa-home"></i>&nbsp;&nbsp;Dashboard</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/profile"><i class="fas fa-user"></i>&nbsp;&nbsp;Profile</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/positions"><i class="fas fa-briefcase"></i>&nbsp;&nbsp;Browse Positions</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/applications/my"><i class="fas fa-file-alt"></i>&nbsp;&nbsp;My Applications</a></li>
            <li><a href="<%= request.getContextPath() %>/ta/favorites"><i class="fas fa-star"></i>&nbsp;&nbsp;Favorites</a></li>
            <li><a href="<%= request.getContextPath() %>/messages/list"><i class="fas fa-comments"></i>&nbsp;&nbsp;Messages</a></li>
            <li>
                <a href="<%= request.getContextPath() %>/ta/notifications">
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
            <p>Manage your personal information, skills, and resume</p>
        </div>
        
        <% if (successMessage != null && !successMessage.isEmpty()) { %>
            <div class="success-message" style="margin-bottom: 25px;">
                <i class="fas fa-check-circle"></i>&nbsp;&nbsp;<%= successMessage %>
            </div>
        <% } %>
        
        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="error-message" style="margin-bottom: 25px;">
                <i class="fas fa-exclamation-circle"></i>&nbsp;&nbsp;<%= errorMessage %>
            </div>
        <% } %>
        
        <div class="card" style="max-width: 700px; margin: 0 auto;">
            <form action="<%= request.getContextPath() %>/ta/profile/update" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="name"><i class="fas fa-user"></i>&nbsp;&nbsp;Full Name</label>
                    <input type="text" id="name" name="name" required 
                           value="<%= user.getName() != null ? user.getName() : "" %>">
                </div>
                
                <div class="form-group">
                    <label for="email"><i class="fas fa-envelope"></i>&nbsp;&nbsp;Email Address</label>
                    <input type="email" id="email" name="email" required 
                           value="<%= user.getEmail() != null ? user.getEmail() : "" %>">
                </div>
                
                <div class="form-group">
                    <label><i class="fas fa-id-badge"></i>&nbsp;&nbsp;Role</label>
                    <input type="text" readonly disabled 
                           value="<%= user.getRole() != null ? user.getRole().toString() : "" %>"
                           style="background-color: #f8fafc; cursor: not-allowed;">
                </div>
                
                <div class="form-group">
                    <label for="skills"><i class="fas fa-star"></i>&nbsp;&nbsp;Skills & Experience</label>
                    <textarea id="skills" name="skills" rows="6" 
                              placeholder="e.g., Java Programming, Database Management, Web Development"><%= user.getSkills() != null ? user.getSkills() : "" %></textarea>
                </div>
                
                <div class="form-group">
                    <label for="cv"><i class="fas fa-file-pdf"></i>&nbsp;&nbsp;Upload Resume</label>
                    <input type="file" id="cv" name="cv" accept=".pdf,.doc,.docx">
                    <% if (user.getCvPath() != null && !user.getCvPath().isEmpty()) { %>
                        <small style="color: #10b981; display: block; margin-top: 8px;">
                            <i class="fas fa-check-circle"></i>&nbsp;&nbsp;Current resume: <a href="<%= request.getContextPath() %>/cv/download?userId=<%= user.getUserId() %>" target="_blank" style="color: #2563eb;">View</a>
                        </small>
                    <% } %>
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
