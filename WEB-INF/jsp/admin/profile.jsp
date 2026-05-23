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
            <h2><i class="fas fa-user-circle"></i>&nbsp;&nbsp;Administrator Profile</h2>
            <p>Manage your administrator account settings</p>
        </div>
        
        <div class="card" style="max-width: 600px; margin: 0 auto;">
            <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
            <% String successMessage = (String) request.getAttribute("successMessage"); %>
            <% String adminRegisterKeyPreview = (String) request.getAttribute("adminRegisterKeyPreview"); %>
            <% String adminKeyErrorMessage = (String) session.getAttribute("adminKeyErrorMessage"); %>
            <% String adminKeySuccessMessage = (String) session.getAttribute("adminKeySuccessMessage"); %>
            <%
                if (adminKeyErrorMessage != null) {
                    session.removeAttribute("adminKeyErrorMessage");
                }
                if (adminKeySuccessMessage != null) {
                    session.removeAttribute("adminKeySuccessMessage");
                }
            %>
            
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
            
            <form action="<%= request.getContextPath() %>/admin/profile" method="post" class="profile-form">
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
                    <input type="text" value="Administrator (Admin)" readonly disabled style="background-color: #f8fafc; cursor: not-allowed;">
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-full">
                        <i class="fas fa-save"></i>&nbsp;&nbsp;Save Changes
                    </button>
                </div>
            </form>
        </div>

        <div class="card" style="max-width: 600px; margin: 24px auto 0;">
            <h3 style="margin-top: 0;">
                <i class="fas fa-key"></i>&nbsp;&nbsp;Admin Registration Key Settings
            </h3>
            <p style="color: #666; margin-top: 0;">
                Update the key required when registering new admin accounts.
            </p>

            <div class="form-group">
                <label><i class="fas fa-eye"></i>&nbsp;&nbsp;Current Key (Visible)</label>
                <input type="text" value="<%= adminRegisterKeyPreview != null ? adminRegisterKeyPreview : "" %>" readonly style="background-color: #f8fafc;">
            </div>

            <% if (adminKeyErrorMessage != null && !adminKeyErrorMessage.isEmpty()) { %>
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i>&nbsp;&nbsp;<%= adminKeyErrorMessage %>
                </div>
            <% } %>

            <% if (adminKeySuccessMessage != null && !adminKeySuccessMessage.isEmpty()) { %>
                <div class="success-message">
                    <i class="fas fa-check-circle"></i>&nbsp;&nbsp;<%= adminKeySuccessMessage %>
                </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/admin/admin-key" method="post">
                <div class="form-group">
                    <label for="newAdminKey"><i class="fas fa-key"></i>&nbsp;&nbsp;New Key</label>
                    <input type="password" id="newAdminKey" name="newAdminKey" minlength="8" required>
                </div>

                <div class="form-group">
                    <label for="confirmNewAdminKey"><i class="fas fa-check"></i>&nbsp;&nbsp;Confirm New Key</label>
                    <input type="password" id="confirmNewAdminKey" name="confirmNewAdminKey" minlength="8" required>
                </div>

                <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-full">
                        <i class="fas fa-save"></i>&nbsp;&nbsp;Update Admin Registration Key
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
