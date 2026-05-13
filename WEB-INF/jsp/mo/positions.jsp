<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruitment.model.User" %>
<%@ page import="com.bupt.tarecruitment.model.Position" %>
<%@ page import="com.bupt.tarecruitment.model.Application" %>
<%@ page import="com.bupt.tarecruitment.dao.UserDAO" %>
<%@ page import="com.bupt.tarecruitment.service.NotificationService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
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
    List<Position> positions = (List<Position>) request.getAttribute("positions");
    
    @SuppressWarnings("unchecked")
    Map<String, List<Application>> selectedApplicationsMap = (Map<String, List<Application>>) request.getAttribute("selectedApplicationsMap");
    
    // Debug output
    System.out.println("=== JSP Debug ===");
    System.out.println("selectedApplicationsMap is null: " + (selectedApplicationsMap == null));
    if (selectedApplicationsMap != null) {
        System.out.println("selectedApplicationsMap size: " + selectedApplicationsMap.size());
    }
    
    // Create UserDAO instance to get TA information
    UserDAO userDAO = new UserDAO();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Positions - TA Recruitment System</title>
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
            <h2><i class="fas fa-briefcase"></i>&nbsp;&nbsp;My Positions</h2>
            <p>Manage and track all your posted TA positions</p>
        </div>
        
        <% if (positions == null || positions.isEmpty()) { %>
            <div class="card">
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h3>No Positions Yet</h3>
                    <p>You haven't posted any TA positions yet. Create your first position to get started.</p>
                    <a href="<%= request.getContextPath() %>/mo/positions/create" class="btn btn-primary">
                        <i class="fas fa-plus-circle"></i>&nbsp;&nbsp;Create First Position
                    </a>
                </div>
            </div>
        <% } else { %>
            <div class="positions-list">
                <% for (Position position : positions) { %>
                    <div class="card position-card-enhanced">
                        <div class="position-header-enhanced">
                            <div class="position-title-section">
                                <h3><%= position.getTitle() %></h3>
                                <span class="badge badge-<%= position.getStatus().toString().toLowerCase() %>">
                                    <%= position.getStatus() == com.bupt.tarecruitment.model.PositionStatus.OPEN ? "Open" : "Closed" %>
                                </span>
                            </div>
                        </div>
                        
                        <div class="position-details-enhanced">
                            <div class="detail-row">
                                <span class="detail-label"><i class="fas fa-align-left"></i>&nbsp;&nbsp;Description:</span>
                                <span class="detail-value"><%= position.getDescription() %></span>
                            </div>
                            
                            <div class="detail-row">
                                <span class="detail-label"><i class="fas fa-list-check"></i>&nbsp;&nbsp;Requirements:</span>
                                <span class="detail-value"><%= position.getRequirements() %></span>
                            </div>
                            
                            <div class="detail-row">
                                <span class="detail-label"><i class="fas fa-clock"></i>&nbsp;&nbsp;Work Hours:</span>
                                <span class="detail-value"><strong style="color: #2563eb; font-size: 1.1rem;"><%= position.getHours() %> hours/week</strong></span>
                            </div>
                            
                            <div class="detail-row">
                                <span class="detail-label"><i class="fas fa-users"></i>&nbsp;&nbsp;Openings:</span>
                                <span class="detail-value"><strong style="color: #2563eb; font-size: 1.1rem;"><%= position.getMaxPositions() %> position(s)</strong></span>
                            </div>
                            
                            <% if (position.getDeadline() != null) { %>
                                <div class="detail-row">
                                    <span class="detail-label"><i class="fas fa-calendar-alt"></i>&nbsp;&nbsp;Application Deadline:</span>
                                    <span class="detail-value">
                                        <strong style="font-size: 1.05rem;"><%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(position.getDeadline()) %></strong>
                                        <% 
                                        int daysRemaining = position.getDaysRemaining();
                                        if (daysRemaining > 7) { %>
                                            <span style="color: #10b981; font-weight: 600; margin-left: 10px;">(<%= daysRemaining %> days left)</span>
                                        <% } else if (daysRemaining >= 4) { %>
                                            <span style="color: #f59e0b; font-weight: 600; margin-left: 10px;">(<%= daysRemaining %> days left)</span>
                                        <% } else if (daysRemaining > 0) { %>
                                            <span style="color: #ef4444; font-weight: 600; margin-left: 10px;">(<%= daysRemaining %> days left - URGENT)</span>
                                        <% } else if (position.isExpired()) { %>
                                            <span style="color: #ef4444; font-weight: bold; margin-left: 10px;">(Expired)</span>
                                        <% } %>
                                    </span>
                                </div>
                            <% } else { %>
                                <div class="detail-row">
                                    <span class="detail-label"><i class="fas fa-calendar-alt"></i>&nbsp;&nbsp;Deadline:</span>
                                    <span class="detail-value" style="color: #64748b;">No deadline set</span>
                                </div>
                            <% } %>
                            
                            <%
                            List<Application> selectedApps = selectedApplicationsMap != null ? selectedApplicationsMap.get(position.getPositionId()) : null;
                            if (selectedApps != null && !selectedApps.isEmpty()) {
                            %>
                                <div class="selected-ta-info">
                                    <div class="info-header">
                                        <i class="fas fa-check-circle"></i>&nbsp;&nbsp;Selected TA(s) (<%= selectedApps.size() %>/<%= position.getMaxPositions() %>)
                                    </div>
                                    <div class="info-content">
                                        <% for (Application selectedApp : selectedApps) {
                                               User selectedTA = userDAO.findById(selectedApp.getTaId());
                                               if (selectedTA == null) continue;
                                        %>
                                            <p class="ta-name"><%= selectedTA.getName() %></p>
                                            <p class="ta-email"><i class="fas fa-envelope"></i>&nbsp;&nbsp;<%= selectedTA.getEmail() %></p>
                                        <% } %>
                                    </div>
                                </div>
                            <% } else { %>
                                <div class="no-ta-info">
                                    <div class="info-header">
                                        <i class="fas fa-exclamation-triangle"></i>&nbsp;&nbsp;No TA Selected
                                    </div>
                                    <p>Please review applications and select a TA for this position.</p>
                                </div>
                            <% } %>
                        </div>
                        
                        <div class="position-actions-enhanced">
                            <a href="<%= request.getContextPath() %>/mo/applications/position?positionId=<%= position.getPositionId() %>" 
                               class="btn btn-secondary">
                                <i class="fas fa-file-alt"></i>&nbsp;&nbsp;View Applications
                            </a>
                            
                            <a href="<%= request.getContextPath() %>/mo/positions/edit?positionId=<%= position.getPositionId() %>" 
                               class="btn btn-primary">
                                <i class="fas fa-edit"></i>&nbsp;&nbsp;Edit Position
                            </a>
                            
                            <form method="post" action="<%= request.getContextPath() %>/mo/positions/delete" 
                                  style="display: inline;" 
                                  onsubmit="return confirm('Are you sure you want to delete this position? This will also delete all related applications.');">
                                <input type="hidden" name="positionId" value="<%= position.getPositionId() %>">
                                <button type="submit" class="btn btn-danger">
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

