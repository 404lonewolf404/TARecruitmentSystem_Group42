<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
    
    Position position = (Position) request.getAttribute("position");
    if (position == null) {
        response.sendRedirect(request.getContextPath() + "/mo/positions/my");
        return;
    }
    
    String errorMessage = (String) request.getAttribute("errorMessage");
    
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
    <title>Edit Position - TA Recruitment System</title>
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
        <div class="card" style="background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%); color: white; border: none;">
            <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 20px;">
                <div>
                    <h2 style="color: white; font-size: 2rem; margin-bottom: 10px;">
                        <i class="fas fa-edit"></i> Edit Position
                    </h2>
                    <p style="font-size: 1.1rem; opacity: 0.95; margin: 0;">
                        <i class="fas fa-info-circle"></i> Modify position information
                    </p>
                </div>
            </div>
        </div>
        
        <% if (errorMessage != null) { %>
            <div class="alert alert-error">
                <%= errorMessage %>
            </div>
        <% } %>
        
        <div class="card">
            <form method="post" action="<%= request.getContextPath() %>/mo/positions/edit" class="form">
                <input type="hidden" name="positionId" value="<%= position.getPositionId() %>">
                
                <div class="form-group">
                    <label for="title">Position Title <span class="required">*</span></label>
                    <input type="text" 
                           id="title" 
                           name="title" 
                           required 
                           maxlength="200"
                           placeholder="e.g., Data Structures Course TA"
                           value="<%= position.getTitle() %>">
                </div>
                
                <div class="form-group">
                    <label for="description">Position Description <span class="required">*</span></label>
                    <textarea id="description" 
                              name="description" 
                              required 
                              rows="5"
                              placeholder="Describe the position responsibilities and work content in detail"><%= position.getDescription() %></textarea>
                </div>
                
                <div class="form-group">
                    <label for="requirements">Position Requirements</label>
                    <textarea id="requirements" 
                              name="requirements" 
                              rows="4"
                              placeholder="Describe the skills and experience requirements for applicants (optional)"><%= position.getRequirements() != null ? position.getRequirements() : "" %></textarea>
                </div>
                
                <div class="form-group">
                    <label for="hours">Work Hours (hours/week) <span class="required">*</span></label>
                    <input type="number" 
                           id="hours" 
                           name="hours" 
                           required 
                           min="1" 
                           max="40"
                           placeholder="e.g., 10"
                           value="<%= position.getHours() %>">
                    <small>Enter weekly work hours (1-40 hours)</small>
                </div>
                
                <div class="form-group">
                    <label for="maxPositions">Number of Openings <span class="required">*</span></label>
                    <input type="number" 
                           id="maxPositions" 
                           name="maxPositions" 
                           required 
                           min="1" 
                           max="100"
                           placeholder="e.g., 2"
                           value="<%= position.getMaxPositions() %>">
                    <small>Enter the number of TAs to recruit (1-100 positions)</small>
                </div>
                
                <div class="form-group">
                    <label for="deadline">Application Deadline (optional)</label>
                    <input type="date" 
                           id="deadline" 
                           name="deadline"
                           min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>"
                           value="<%= position.getDeadline() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(position.getDeadline()) : "" %>">
                    <small>After setting an application deadline, expired positions will automatically stop accepting applications</small>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                    <a href="<%= request.getContextPath() %>/mo/positions/my" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
    
    <script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
