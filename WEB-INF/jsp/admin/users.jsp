<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.UserRole" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
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
    
    @SuppressWarnings("unchecked")
    List<User> users = (List<User>) request.getAttribute("users");
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - TA Recruitment System</title>
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
            <h2><i class="fas fa-users"></i>&nbsp;&nbsp;User Management</h2>
            <p>View and manage all users in the system</p>
        </div>
        
        <% if (users != null && !users.isEmpty()) { %>
            <div class="card">
                <div class="stats-info">
                    <p style="font-size: 1.1rem; color: #64748b;">
                        <i class="fas fa-chart-bar"></i>&nbsp;&nbsp;Total Users: <strong style="color: #2563eb; font-size: 1.3rem;"><%= users.size() %></strong>
                    </p>
                </div>
            </div>
            
            <div class="card">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th><i class="fas fa-user"></i>&nbsp;&nbsp;Name</th>
                            <th><i class="fas fa-envelope"></i>&nbsp;&nbsp;Email</th>
                            <th><i class="fas fa-id-badge"></i>&nbsp;&nbsp;Role</th>
                            <th><i class="fas fa-star"></i>&nbsp;&nbsp;Skills</th>
                            <th><i class="fas fa-file-pdf"></i>&nbsp;&nbsp;Resume</th>
                            <th><i class="fas fa-calendar"></i>&nbsp;&nbsp;Registered</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (User user : users) { %>
                        <tr>
                            <td><strong><%= user.getName() %></strong></td>
                            <td><%= user.getEmail() %></td>
                            <td>
                                <% 
                                String roleText = "";
                                String roleClass = "";
                                if (user.getRole() == UserRole.TA) {
                                    roleText = "Teaching Assistant";
                                    roleClass = "badge-selected";
                                } else if (user.getRole() == UserRole.MO) {
                                    roleText = "Module Owner";
                                    roleClass = "badge-pending";
                                } else if (user.getRole() == UserRole.ADMIN) {
                                    roleText = "Administrator";
                                    roleClass = "badge-rejected";
                                }
                                %>
                                <span class="badge <%= roleClass %>"><%= roleText %></span>
                            </td>
                            <td>
                                <% if (user.getRole() == UserRole.TA) { %>
                                    <%= user.getSkills() != null && !user.getSkills().isEmpty() ? user.getSkills() : "Not filled" %>
                                <% } else { %>
                                    <span style="color: #94a3b8;">-</span>
                                <% } %>
                            </td>
                            <td>
                                <% if (user.getRole() == UserRole.TA) { %>
                                    <% if (user.getCvPath() != null && !user.getCvPath().isEmpty()) { %>
                                        <span style="color: #10b981; font-weight: 600;"><i class="fas fa-check-circle"></i>&nbsp;&nbsp;Uploaded</span>
                                    <% } else { %>
                                        <span style="color: #ef4444; font-weight: 600;"><i class="fas fa-times-circle"></i>&nbsp;&nbsp;Not uploaded</span>
                                    <% } %>
                                <% } else { %>
                                    <span style="color: #94a3b8;">-</span>
                                <% } %>
                            </td>
                            <td><%= user.getCreatedAt() != null ? dateFormat.format(user.getCreatedAt()) : "-" %></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
                
                <div style="margin-top: 25px; padding: 20px; background-color: #f8fafc; border-radius: 8px; border-left: 4px solid #2563eb;">
                    <p style="margin: 0; color: #475569; font-size: 0.95rem;">
                        <i class="fas fa-info-circle"></i>&nbsp;&nbsp;<strong>Note:</strong> Skills column only applies to TAs | Resume status only applies to TAs
                    </p>
                </div>
            </div>
        <% } else { %>
            <div class="card">
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h3>No Users</h3>
                    <p>There are no users in the system yet.</p>
                </div>
            </div>
        <% } %>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
